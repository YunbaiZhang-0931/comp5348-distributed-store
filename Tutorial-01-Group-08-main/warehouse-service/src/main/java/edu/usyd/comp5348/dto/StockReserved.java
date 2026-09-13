package edu.usyd.comp5348.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * 当订单库存预留成功后发布的事件。
 */
@Data
@Builder
public class StockReserved {
    private String eventId;        // 唯一事件ID
    private Instant occurredAt;    // 事件发生时间
    private String correlationId;  // 关联ID（通常是 orderId）

    private String orderId;
    private String itemId;
    private int quantity;
    private BigDecimal amount;

    /** 静态工厂方法 */
    public static StockReserved of(String orderId, String itemId, int qty, BigDecimal amount) {
        return StockReserved.builder()
                .orderId(orderId)
                .itemId(itemId)
                .quantity(qty)
                .amount(amount)
                .build();
    }
}