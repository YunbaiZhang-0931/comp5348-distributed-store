package com.comp5348.bank.messaging.events;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * 支付失败事件。
 * 当账户余额不足或扣款失败时发布。
 */
@Data
@Builder
public class PaymentFailed {
    private String eventId;
    private Instant occurredAt;
    private String orderId;
    private BigDecimal amount;
    private String reason;

    public static PaymentFailed of(String orderId, BigDecimal amount, String reason) {
        return PaymentFailed.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(Instant.now())
                .orderId(orderId)
                .amount(amount)
                .reason(reason)
                .build();
    }
}
