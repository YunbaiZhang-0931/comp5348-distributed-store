package com.comp5348.bank.messaging.events;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * 支付成功事件。
 * 当扣款操作成功后，由 BankService 写入 Outbox 并发布。
 */
@Data
@Builder
public class PaymentCompleted {
    private String eventId;
    private Instant occurredAt;
    private String orderId;
    private BigDecimal amount;

    public static PaymentCompleted of(String orderId, BigDecimal amount) {
        return PaymentCompleted.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(Instant.now())
                .orderId(orderId)
                .amount(amount)
                .build();
    }
}
