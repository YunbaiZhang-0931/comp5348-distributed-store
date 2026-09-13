package com.comp5348.bank.dto;


import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

/** 通用配送状态事件 */
@Data
@Builder
public class DeliveryStatusUpdated {
    private String eventId;
    private Instant occurredAt;
    private String orderId;
    private String warehouseId;
    private String status;

    public static DeliveryStatusUpdated of(String orderId, String warehouseId, String status) {
        return DeliveryStatusUpdated.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(Instant.now())
                .orderId(orderId)
                .warehouseId(warehouseId)
                .status(status)
                .build();
    }
}

