package com.comp5348.bank.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

/** 供 Outbox 序列化的简化事件格式 */
@Data
@Builder
public class SimpleDeliveryEvent {
    private String eventId;
    private Instant occurredAt;
    private String orderId;
    private String warehouseId;
    private String status;
}

