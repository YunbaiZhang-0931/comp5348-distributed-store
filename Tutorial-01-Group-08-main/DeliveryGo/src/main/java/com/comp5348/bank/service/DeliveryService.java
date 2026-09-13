package com.comp5348.bank.service;

import com.comp5348.bank.dto.DeliveryStatusUpdated;
import com.comp5348.bank.model.Outbox;
import com.comp5348.bank.model.Shipment;
import com.comp5348.bank.repository.OutboxRepository;
import com.comp5348.bank.repository.ShipmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.usyd.comp5348.Topics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final ShipmentRepository shipmentRepo;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper om;
    private final Random random = new Random();

    /**
     * 处理发货请求：创建配送记录并发布第一个事件
     */
    @Transactional
    public void handleDeliveryRequest(String orderId, String warehouseId) {
        Shipment shipment = shipmentRepo.save(Shipment.builder()
                .orderId(orderId)
                .warehouseId(warehouseId)
                .status(Shipment.Status.REQUESTED)
                .build());

        publishEvent(shipment, Topics.DeliveryEvt.RECEIVED, Shipment.Status.RECEIVED);

        // 模拟配送进度（取件 -> 派送中 -> 已送达）
        simulateDelivery(shipment);
    }

    private void simulateDelivery(Shipment shipment) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            try {
                step(shipment, Topics.DeliveryEvt.PICKED_UP, Shipment.Status.PICKED_UP);
                Thread.sleep(5000);
                step(shipment, Topics.DeliveryEvt.OUT_FOR_DELIVERY, Shipment.Status.OUT_FOR_DELIVERY);
                Thread.sleep(5000);
                step(shipment, Topics.DeliveryEvt.DELIVERED, Shipment.Status.DELIVERED);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, 5, TimeUnit.SECONDS);
    }

    private void step(Shipment shipment, String topic, Shipment.Status newStatus) {
        if (random.nextDouble() < 0.05) { // 5% 丢包率
            publishEvent(shipment, Topics.DeliveryEvt.FAILED_IN_TRANSIT, Shipment.Status.FAILED);
            return;
        }
        shipment.setStatus(newStatus);
        shipmentRepo.save(shipment);
        publishEvent(shipment, topic, newStatus);
    }

    private void publishEvent(Shipment shipment, String topic, Shipment.Status status) {
        try {
            var event = DeliveryStatusUpdated.of(
                    shipment.getOrderId(),
                    shipment.getWarehouseId(),
                    status.name()
            );
            String payload = om.writeValueAsString(event);
            outboxRepo.save(Outbox.builder()
                    .aggregateId(shipment.getOrderId())
                    .type(topic)
                    .payload(payload)
                    .status(Outbox.Status.NEW)
                    .build());
            System.out.println("Published " + topic + " for order " + shipment.getOrderId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize delivery event", e);
        }
    }
}

