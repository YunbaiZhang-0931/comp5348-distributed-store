package edu.usyd.comp5348.store_api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class OrderCreated {
    private String eventId;
    private Instant occurredAt;
    private String correlationId;
    private String orderId;
    private String userId;
    private String itemId;
    private int quantity;
    private BigDecimal amount;
}
