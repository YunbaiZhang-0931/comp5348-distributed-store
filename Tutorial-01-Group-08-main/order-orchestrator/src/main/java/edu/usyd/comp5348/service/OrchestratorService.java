package edu.usyd.comp5348.service;

import com.fasterxml.jackson.databind.*;

import edu.usyd.comp5348.Topics;
import edu.usyd.comp5348.entity.Outbox;
import edu.usyd.comp5348.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrchestratorService {

    private final OutboxRepository outbox;
    private final ObjectMapper om;
    private final RestTemplate restTemplate = new RestTemplate();


    // === 订单创建 → 通知仓库预留 ===
    @Transactional
    public void onOrderCreated(JsonNode evt) {
        String orderId = evt.get("orderId").asText();
        String itemId = evt.get("itemId").asText();
        int quantity = evt.get("quantity").asInt();
        BigDecimal amount = evt.get("amount").decimalValue();

        log.info("🟦 [Orchestrator] OrderCreated received for {}", orderId);
        emit(Topics.WarehouseCmd.RESERVE_REQUEST, orderId, Map.of("eventId", uuid(), "occurredAt", Instant.now().toString(), "orderId", orderId, "itemId", itemId, "quantity", quantity, "amount", amount));

//        emit(Topics.WarehouseCmd.CHECK_REQUEST, orderId, Map.of("eventId", uuid(), "occurredAt", Instant.now().toString(), "orderId", orderId, "itemId", itemId, "quantity", quantity, "amount", amount));
    }

    // === 预留成功 → 让银行扣款 ===
    @Transactional
    public void onStockReserved(JsonNode evt) {
        String orderId = evt.get("orderId").asText();
        BigDecimal amount = evt.has("amount") ? evt.get("amount").decimalValue() : new BigDecimal("99.99");

        log.info("✅ [Orchestrator] Stock reserved for order {}", orderId);

//        emitStore(Topics.StoreCmd.ORDER_RESERVED, orderId);

        emit(Topics.BankCmd.PAYMENT_REQUEST, orderId, Map.of("eventId", uuid(), "occurredAt", Instant.now().toString(), "orderId", orderId, "fromAccountNo", "1", "toAccountNo", "2", "amount", amount));
    }

    @Transactional
    public void onOutOfStock(JsonNode evt) {
        String orderId = evt.get("orderId").asText();
        log.warn("🟥 [Orchestrator] Out of stock for order {}", orderId);


        emit(Topics.WarehouseCmd.RELEASE_REQUEST, orderId, Map.of("orderId", orderId));
        emit(Topics.StoreCmd.ORDER_RELEASED, orderId, Map.of("eventId", uuid(), "occurredAt", Instant.now().toString(), "orderId", orderId, "reason", "OUT_OF_STOCK"));
    }


    // === 支付成功 → 通知配送 ===
    @Transactional
    public void onPaid(JsonNode evt) {
        String orderId = evt.get("orderId").asText();
        String warehouseId = evt.has("warehouseId") ? evt.get("warehouseId").asText() : "WH-1";

        log.info("💰 [Orchestrator] Payment successful for order {}", orderId);

        emitStore(Topics.StoreCmd.ORDER_PAID, orderId);

// 启动异步任务，在10秒后发送发货相关事件
        CompletableFuture.runAsync(() -> {
            try {
                // 等待10秒（可视为客户可取消时间窗口）
                Thread.sleep(10_000);

                // 检查是否已经取消
                String url = "http://localhost:8080/orders/" + orderId;
                ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
                String status = response.getBody().path("status").asText();

                if ("CANCELLED".equalsIgnoreCase(status) || "CANCEL_REQUESTED".equalsIgnoreCase(status)) {
                    log.warn("🛑 [Orchestrator] Order {} was cancelled before delivery. Skipping dispatch.", orderId);
                    return; // 不继续发货
                }

                // 发货请求
                emit(Topics.DeliveryCmd.REQUESTED, orderId, Map.of("eventId", uuid(), "occurredAt", Instant.now().toString(), "orderId", orderId, "warehouseId", warehouseId));

                // 同时让 Store 知道发货请求已发送
                emitStore(Topics.StoreCmd.ORDER_DELIVERY_REQUESTED, orderId);

                log.info("🚚 [Orchestrator] Delivery request sent after 10s for order {}", orderId);

            } catch (InterruptedException e) {
                log.error("❌ [Orchestrator] Delay interrupted for order {}", orderId, e);
                Thread.currentThread().interrupt();
            }
        });

    }

    @Transactional
    public void onOrderCancelled(JsonNode evt) {
        String orderId = evt.get("orderId").asText();
        log.warn("🟥 [Orchestrator] Received cancel request for {}", orderId);

        try {
            // 1️⃣ 调用 Store API 获取订单状态
            String url = "http://localhost:8080/orders/" + orderId;
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            String status = response.getBody().path("status").asText();
            String email = evt.path("email").asText("customer@example.com");

            log.info("📦 [Orchestrator] Current order {} status = {}", orderId, status);

            // 3️⃣ 可取消 — 根据状态决定是否退款
            if ("CREATED".equals(status)) {
                log.info("🟨 [Orchestrator] Order {} cancelled before payment — no stock release or refund needed", orderId);
                emitStore(Topics.StoreCmd.ORDER_RELEASED, orderId); // Store 改状态即可
                return;
            }

            // 2️⃣ 判断是否已发货（或完成）
            if ("DELIVERY_REQUESTED".equals(status) || "DELIVERED".equals(status) || "COMPLETED".equals(status)) {
                log.warn("❌ Too late to cancel, delivery already requested for {}", orderId);
                emitStore(Topics.StoreCmd.ORDER_CANCEL_REJECTED, orderId);
                return;
            }

            // 4️⃣ 通知仓库释放库存 + 通知Store更新状态
            emit(Topics.WarehouseCmd.RELEASE_REQUEST, orderId, Map.of("orderId", orderId));

            log.info("✅ [Orchestrator] Cancellation executed for {}", orderId);

        } catch (Exception e) {
            log.error("❌ [Orchestrator] Failed to check order status or process cancel", e);
        }
    }


    // === ❌ 支付失败 → 通知仓库释放库存 ===
    @Transactional
    public void onPaymentFailed(JsonNode evt) {
        String orderId = evt.get("orderId").asText();
        String email = evt.path("email").asText("customer@example.com");

        log.warn("🚨 [Orchestrator] Payment failed for order {}", orderId);

        emit(Topics.WarehouseCmd.RELEASE_REQUEST, orderId, Map.of("eventId", uuid(), "occurredAt", Instant.now().toString(), "orderId", orderId));
        emitStore(Topics.StoreCmd.ORDER_PAYMENT_FAILED, orderId);
    }

    // === 📦 仓库释放/预留失败 → 尝试退款（或直接标记失败） ===
    @Transactional
    public void onStockReleased(JsonNode evt) {
        String orderId = evt.get("orderId").asText();
        log.warn("📦 [Orchestrator] Stock released for order {}", orderId);

        try {
            // 1️⃣ 调用 Store API 获取订单状态
            String url = "http://localhost:8080/orders/" + orderId;
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
            String status = response.getBody().path("status").asText();

            // 2️⃣ 判断是否已支付
            if ("PAID".equalsIgnoreCase(status)) {
                log.info("💸 [Orchestrator] Order {} was paid — issuing refund", orderId);

                emit(Topics.BankCmd.REFUND_REQUEST, orderId, Map.of("eventId", uuid(), "occurredAt", Instant.now().toString(), "orderId", orderId));
            } else {
                log.info("🟨 [Orchestrator] Order {} not paid — no refund needed", orderId);
            }

            // 3️⃣ 无论如何都通知 Store 更新状态
            emitStore(Topics.StoreCmd.ORDER_RELEASED, orderId);

        } catch (Exception e) {
            log.error("❌ [Orchestrator] Failed to process stock release for {}", orderId, e);
        }
    }


    @Transactional
    public void onDeliveryFailed(JsonNode evt) {
        String orderId = evt.get("orderId").asText();
        String failureType = evt.get("failureType").asText(); // 由 Delivery 事件携带
        String email = evt.path("email").asText("customer@example.com");

        log.error("🚨 [Orchestrator] Delivery failed for {} ({})", orderId, failureType);

        emitStore(Topics.StoreCmd.ORDER_DELIVERY_FAILED, orderId);

        emit(Topics.BankCmd.REFUND_REQUEST, orderId, Map.of("orderId", orderId));
        log.warn("📦 [Orchestrator] Delivery lost in transit for {} — refund issued, no stock rollback", orderId);
    }


    // === 🚚 配送完成 → 通知仓库扣减库存并更新订单为 COMPLETED ===
    @Transactional
    public void onDelivered(JsonNode evt) {
        String orderId = evt.get("orderId").asText();
        String email = evt.path("email").asText("customer@example.com");

        log.info("🚚 [Orchestrator] Delivery completed for order {}", orderId);

        emitStore(Topics.StoreCmd.ORDER_DELIVERED, orderId);


        // 最终扣减库存
        emit(Topics.WarehouseCmd.FINALIZE_REQUEST, orderId, Map.of("eventId", uuid(), "occurredAt", Instant.now().toString(), "orderId", orderId));
    }

    @Transactional
    public void onFinalized(JsonNode evt) {
        String orderId = evt.get("orderId").asText();
        log.info("🧾 [Orchestrator] Order finalized for {}", orderId);
        emitStore(Topics.StoreCmd.ORDER_FINALIZED, orderId);
    }

    // === 工具方法 ===
    private void emit(String routingKey, String orderId, Map<String, Object> payload) {
        try {
            outbox.save(Outbox.builder().aggregateId(orderId).type(routingKey).payload(om.writeValueAsString(payload)).status(Outbox.Status.NEW).build());
            log.info("📤 Event queued to Outbox: {} for order {}", routingKey, orderId);
        } catch (Exception e) {
            log.error("❌ Failed to write Outbox event", e);
            throw new RuntimeException(e);
        }
    }

    private void emitStore(String routingKey, String orderId) {
        emit(routingKey, orderId, Map.of("eventId", uuid(), "occurredAt", Instant.now().toString(), "orderId", orderId));
    }


    private static String uuid() {
        return UUID.randomUUID().toString();
    }
}