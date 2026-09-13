package edu.usyd.comp5348.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

/**
 * 当订单库存释放时发布的事件。
 */
@Data
@Builder
public class StockReleased {
    private String eventId;
    private Instant occurredAt;
    private String correlationId;

    private String orderId;

    public static StockReleased of(String orderId) {
        return StockReleased.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(Instant.now())
                .correlationId(orderId)
                .orderId(orderId)
                .build();
    }
}