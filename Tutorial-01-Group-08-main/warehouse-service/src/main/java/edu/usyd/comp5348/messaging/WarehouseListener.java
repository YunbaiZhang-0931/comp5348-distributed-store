package edu.usyd.comp5348.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usyd.comp5348.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class WarehouseListener {

    private final WarehouseService warehouseService;
    private final ObjectMapper om;

    /**
     * 监听 warehouse.reserve.request → 执行库存预留
     */
    @RabbitListener(queues = "warehouse.reserve.request.queue")
    public void onReserveRequest(String message) {
        try {
            var evt = om.readTree(message);
            String orderId = evt.get("orderId").asText();
            String itemId = evt.get("itemId").asText();
            int quantity = evt.get("quantity").asInt();
            BigDecimal amount = evt.has("amount") ? evt.get("amount").decimalValue() : BigDecimal.ZERO;

            log.info("📦 [Warehouse] Received warehouse.reserve.request for {} (amount = {})", orderId, amount);

            warehouseService.reserve(orderId, itemId, quantity, amount);
        } catch (Exception e) {
            log.error("[Warehouse] Reserve failed: {}", message, e);
            try {
                var evt = om.readTree(message);
                String orderId = evt.get("orderId").asText();
                String itemId = evt.get("itemId").asText();
                int qty = evt.get("quantity").asInt();
                BigDecimal amount = evt.has("amount") ? evt.get("amount").decimalValue() : BigDecimal.ZERO;

                warehouseService.publishOutOfStock(orderId, itemId, qty, amount);
            } catch (Exception ex) {
                log.error("[Warehouse] Fallback publish failed", ex);
            }
        }
    }


    @RabbitListener(queues = "warehouse.check.request.queue")
    public void onCheckRequest(String message) {
        try {
            var evt = om.readTree(message);
            String orderId = evt.get("orderId").asText();
            String itemId = evt.get("itemId").asText();
            int quantity = evt.get("quantity").asInt();
            BigDecimal amount = evt.has("amount") ? evt.get("amount").decimalValue() : BigDecimal.ZERO;
            warehouseService.check(orderId, itemId, quantity, amount);
        } catch (Exception e) {
            log.error("[Warehouse] Fallback publish failed", e);
        }

    }

    /**
     * 监听 warehouse.release.request → 释放库存
     */
    @RabbitListener(queues = "warehouse.release.request.queue")
    public void onReleaseRequest(String message) {
        try {
            var evt = om.readTree(message);
            String orderId = evt.get("orderId").asText();
            log.info("[Warehouse] Release request for {}", orderId);

            warehouseService.release(orderId);
        } catch (Exception e) {
            log.error("[Warehouse] Failed to handle release.request", e);
        }

    }

    /**
     * 监听 warehouse.finalize.request → 最终扣减库存
     */
    @RabbitListener(queues = "warehouse.finalize.request.queue")
    public void onFinalizeRequest(String message) {
        try {
            var evt = om.readTree(message);
            String orderId = evt.get("orderId").asText();
            log.info("[Warehouse] Finalize request for {}", orderId);

            warehouseService.finalizeReservation(orderId);
        } catch (Exception e) {
            log.error("[Warehouse] Failed to handle finalize.request", e);
        }
    }
}

