package edu.usyd.comp5348.messaging;

import com.fasterxml.jackson.databind.*;

import edu.usyd.comp5348.Topics;
import edu.usyd.comp5348.service.OrchestratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j @Component @RequiredArgsConstructor
public class OrchestratorListener {

    private final OrchestratorService svc; private final ObjectMapper om;

    @RabbitListener(queues = "orchestrator.events")
    public void onEvent(String payload, @Header("amqp_receivedRoutingKey") String rk) throws Exception {
        JsonNode evt = om.readTree(payload);
        log.info("[Orch] recv {} -> {}", rk, payload);

        switch (rk) {
            // Store
            case Topics.StoreEvt.ORDER_CREATED -> svc.onOrderCreated(evt);
            case Topics.StoreEvt.ORDER_CANCEL_REQUEST -> svc.onOrderCancelled(evt);

            // Warehouse
            case Topics.WarehouseEvt.RESERVED -> svc.onStockReserved(evt);
            case Topics.WarehouseEvt.RELEASED -> svc.onStockReleased(evt);
            case Topics.WarehouseEvt.FINALIZED -> svc.onFinalized(evt);
            case Topics.WarehouseEvt.OUT_OF_STOCK -> svc.onOutOfStock(evt);

            // Bank
            case Topics.BankEvt.PAID -> svc.onPaid(evt);
            case Topics.BankEvt.PAYMENT_FAILED -> svc.onPaymentFailed(evt);
            case Topics.BankEvt.REFUNDED -> {/* 可通知用户 */ break;}

            // Delivery
            case Topics.DeliveryEvt.DELIVERED -> svc.onDelivered(evt);
            case Topics.DeliveryEvt.FAILED_IN_TRANSIT -> svc.onDeliveryFailed(evt);
            case Topics.DeliveryEvt.RECEIVED, Topics.DeliveryEvt.PICKED_UP, Topics.DeliveryEvt.OUT_FOR_DELIVERY -> {
                /* 可转发给 EmailService 发送“已取件/派送中”等通知事件 */
            }

            default -> log.warn("[Orch] Unknown routing key: {}", rk);
        }
    }
}

