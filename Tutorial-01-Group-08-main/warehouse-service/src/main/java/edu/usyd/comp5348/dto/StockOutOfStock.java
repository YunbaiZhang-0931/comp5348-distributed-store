package edu.usyd.comp5348.dto;

import java.math.BigDecimal;

public record StockOutOfStock(String orderId, String itemId, int quantity, BigDecimal amount) {

    public static StockOutOfStock of(String orderId, String itemId, int qty, BigDecimal amount) {
        return new StockOutOfStock(orderId, itemId, qty, amount);
    }
}

