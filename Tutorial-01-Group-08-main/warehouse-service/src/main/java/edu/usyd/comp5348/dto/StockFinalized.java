package edu.usyd.comp5348.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

/**
 * 当订单完成且库存最终扣减时发布的事件。
 */
@Data
@Builder
public class StockFinalized {
    private String eventId;
    private Instant occurredAt;
    private String correlationId;

    private String orderId;

    public static StockFinalized of(String orderId) {
        return StockFinalized.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(Instant.now())
                .correlationId(orderId)
                .orderId(orderId)
                .build();
    }
}
