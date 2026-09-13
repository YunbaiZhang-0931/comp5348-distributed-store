package com.comp5348.bank.controller;

import com.comp5348.bank.dto.PaymentRequest;
import com.comp5348.bank.dto.RefundRequest;
import com.comp5348.bank.dto.TransactionStatusResponse;
import com.comp5348.bank.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bank")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestBody PaymentRequest req) {
        bankService.pay(req.getOrderNo(), req.getFromAccountNo(), req.getToAccountNo(), req.getAmount());
        return ResponseEntity.ok("Payment processed for order " + req.getOrderNo());
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refund(@RequestBody RefundRequest req) {
        bankService.refund(req.getOrderNo());
        return ResponseEntity.ok("Refund processed for order " + req.getOrderNo());
    }

    @GetMapping("/status/{orderNo}")
    public ResponseEntity<?> status(@PathVariable String orderNo) {
        String status = bankService.getStatus(orderNo);
        return ResponseEntity.ok(new TransactionStatusResponse(orderNo, status));
    }
}

