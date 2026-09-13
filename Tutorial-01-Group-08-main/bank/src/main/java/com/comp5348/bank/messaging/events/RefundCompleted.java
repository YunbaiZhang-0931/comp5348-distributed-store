package com.comp5348.bank.messaging.events;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * 退款完成事件。
 * 当 Orchestrator 发起退款请求并成功退回金额后发布。
 */
@Data
@Builder
public class RefundCompleted {
    private String eventId;
    private Instant occurredAt;
    private String orderId;
    private BigDecimal amount;

    public static RefundCompleted of(String orderId, BigDecimal amount) {
        return RefundCompleted.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(Instant.now())
                .orderId(orderId)
                .amount(amount)
                .build();
    }
}
