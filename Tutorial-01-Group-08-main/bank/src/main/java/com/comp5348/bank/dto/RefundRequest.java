package com.comp5348.bank.dto;

import lombok.Data;

@Data
public class RefundRequest {
    private String orderNo;  // 退款对应的订单号
}
