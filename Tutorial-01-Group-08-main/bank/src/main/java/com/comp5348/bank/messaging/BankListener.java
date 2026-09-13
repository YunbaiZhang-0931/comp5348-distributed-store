package com.comp5348.bank.messaging;

import com.comp5348.bank.service.BankService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class BankListener {

    private final BankService bankService;
    private final ObjectMapper om;

    /** 监听 orchestrator 发出的 bank.payment.request 事件 */
    @RabbitListener(queues = "bank.payment.request.queue")
    public void onPaymentRequest(String message) {
        try {
            log.info("💰 [Bank] Received payment request: {}", message);

            var evt = om.readTree(message);
            String orderId = evt.get("orderId").asText();
            String fromAccountNo = evt.get("fromAccountNo").asText();
            String toAccountNo = evt.get("toAccountNo").asText();
            BigDecimal amount = evt.get("amount").decimalValue();

            // 执行支付逻辑
            bankService.pay(orderId, fromAccountNo, toAccountNo, amount);

        } catch (Exception e) {
            log.error("❌ [Bank] Failed to process payment request", e);
        }
    }

    /** 监听 orchestrator 发出的 bank.refund.request 事件 */
    @RabbitListener(queues = "bank.refund.request.queue")
    public void onRefundRequest(String message) {
        try {
            log.info("💸 [Bank] Received refund request: {}", message);

            var evt = om.readTree(message);
            String orderId = evt.get("orderId").asText();

            bankService.refund(orderId);

        } catch (Exception e) {
            log.error("❌ [Bank] Failed to process refund request", e);
        }
    }
}

