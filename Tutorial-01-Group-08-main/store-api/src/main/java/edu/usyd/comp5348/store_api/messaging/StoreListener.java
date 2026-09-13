package edu.usyd.comp5348.store_api.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usyd.comp5348.Topics;
import edu.usyd.comp5348.store_api.domain.Order;
import edu.usyd.comp5348.store_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 🎧 StoreListener
 * 监听 Orchestrator / Bank / Warehouse / DeliveryCo 的事件，
 * 主动更新订单状态。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StoreListener {

    private final ObjectMapper om;
    private final OrderService orders;

    @RabbitListener(queues = "store.order.events.queue")
    public void onOrderEvent(Message message) {
        try {
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            JsonNode evt = om.readTree(new String(message.getBody()));
            String orderId = evt.path("orderId").asText();

            switch (routingKey) {
                case Topics.StoreEvt.ORDER_CREATED -> update(orderId, Order.Status.CREATED);
                case Topics.StoreCmd.ORDER_RESERVED -> update(orderId, Order.Status.RESERVED);
                case Topics.StoreCmd.ORDER_RELEASED -> update(orderId, Order.Status.CANCELLED);
                case Topics.StoreCmd.ORDER_PAID -> update(orderId, Order.Status.PAID);
                case Topics.StoreCmd.ORDER_DELIVERY_REQUESTED -> update(orderId, Order.Status.DISPATCH_REQUESTED);
                case Topics.StoreCmd.ORDER_PAYMENT_FAILED -> update(orderId, Order.Status.CANCELLED);
                case Topics.StoreCmd.ORDER_DELIVERED -> update(orderId, Order.Status.DELIVERED);
                case Topics.StoreCmd.ORDER_DELIVERY_FAILED -> update(orderId, Order.Status.CANCELLED);
                case Topics.StoreCmd.ORDER_FINALIZED -> update(orderId, Order.Status.COMPLETED);
                case Topics.StoreCmd.ORDER_CANCEL_REJECTED -> {
                    log.warn("🚫 [StoreListener] Cancellation rejected for order {}", orderId);
                }
                default -> log.warn("⚠️ [StoreListener] Unknown routing key: {}", routingKey);
            }

        } catch (Exception e) {
            log.error("❌ [StoreListener] Failed to process order event", e);
        }
    }

    private void update(String orderId, Order.Status status) {
        log.info("📦 [StoreListener] Updating order {} -> {}", orderId, status);
        orders.updateOrderStatus(orderId, status);
    }
}

