package edu.usyd.comp5348.store_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usyd.comp5348.Topics;
import edu.usyd.comp5348.store_api.dto.OrderCreated;
import edu.usyd.comp5348.store_api.domain.Order;
import edu.usyd.comp5348.store_api.domain.Outbox;
import edu.usyd.comp5348.store_api.repo.OrderRepo;
import edu.usyd.comp5348.store_api.repo.OutboxRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
@Slf4j
@Service
public class OrderService {
    private final OrderRepo orders;
    private final OutboxRepo outbox;
    private final ObjectMapper om;
    public OrderService(OrderRepo orders, OutboxRepo outbox, ObjectMapper om){
        this.orders = orders;
        this.outbox = outbox;
        this.om = om;
    }

    @Transactional
    public Order create(String userId, String itemId, int quantity, BigDecimal unitPrice) {
        // Create Order
        var o = orders.save(Order.builder()
                .userId(userId).itemId(itemId).quantity(quantity)
                .amount(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                .status(Order.Status.CREATED).build());

        try {
            // Create Event
            var evt = OrderCreated.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(Instant.now())
                    .correlationId(o.getId())
                    .orderId(o.getId()).userId(userId).itemId(itemId)
                    .quantity(quantity).amount(o.getAmount()).build();

            // Save Event
            outbox.save(Outbox.builder()
                    .aggregateId(o.getId())
                    .type(Topics.StoreEvt.ORDER_CREATED)
                    .payload(om.writeValueAsString(evt))
                    .status(Outbox.Status.NEW).build());
        } catch (Exception e){
            throw new RuntimeException(e);
        }

        return o;
    }

    @Transactional
    public void cancel(String orderId) {
        var order = orders.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getStatus().isCancelable()) {
            throw new RuntimeException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        // 仅发出取消请求事件，不直接释放库存或退款
        outbox.save(Outbox.builder()
                .aggregateId(orderId)
                .type(Topics.StoreEvt.ORDER_CANCEL_REQUEST)
                .payload(String.format("{\"orderId\":\"%s\",\"occurredAt\":\"%s\"}",
                        orderId, Instant.now().toString()))
                .status(Outbox.Status.NEW)
                .build());

        log.info("📤 [Store] Emitted cancel request for order {}", orderId);
    }



    @Transactional
    public void updateOrderStatus(String orderId, Order.Status newStatus) {
        Order order = orders.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(newStatus);
        orders.save(order);
    }

}