package com.comp5348.bank.dto;

import lombok.Data;

/** 发货请求事件 */
@Data
public class DeliveryRequested {
    private String eventId;
    private String orderId;
    private String warehouseId;
}