package com.comp5348.bank.messaging;

import com.comp5348.bank.dto.DeliveryRequested;
import com.comp5348.bank.service.DeliveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usyd.comp5348.Topics;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryListener {

    private final DeliveryService deliveryService;
    private final ObjectMapper om;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "delivery.requests", durable = "true"),
            exchange = @Exchange(value = Topics.DELIVERY_EXCHANGE, type = "topic"),
            key = Topics.DeliveryCmd.REQUESTED
    ))
    public void onDeliveryRequested(String payload) {
        try {
            var req = om.readValue(payload, DeliveryRequested.class);
            System.out.println("Received delivery request for order " + req.getOrderId());
            deliveryService.handleDeliveryRequest(req.getOrderId(), req.getWarehouseId());
        } catch (Exception e) {
            System.err.println("❌ Failed to parse delivery request: " + e.getMessage());
        }
    }
}