package edu.usyd.comp5348.dto;

import lombok.Data;

@Data
public class ReserveRequest {
    private String orderId;
    private String itemId;
    private int quantity;
}