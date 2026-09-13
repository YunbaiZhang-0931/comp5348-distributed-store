package com.comp5348.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionStatusResponse {
    private String orderNo;
    private String status;
}
