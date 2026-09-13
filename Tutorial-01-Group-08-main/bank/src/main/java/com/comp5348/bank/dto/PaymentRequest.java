package com.comp5348.bank.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private String orderNo;        // 订单号
    private String fromAccountNo;  // 付款账户ID
    private String toAccountNo;    // 收款账户ID
    private BigDecimal amount;     // 金额
}
