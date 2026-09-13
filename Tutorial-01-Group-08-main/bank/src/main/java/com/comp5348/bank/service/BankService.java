package com.comp5348.bank.service;

import com.comp5348.bank.model.TransactionRecord;
import com.comp5348.bank.repository.AccountRepository;
import com.comp5348.bank.repository.TransactionRecordRepository;
import com.comp5348.bank.repository.OutboxRepository;
import com.comp5348.bank.model.Outbox;
import com.comp5348.bank.messaging.events.PaymentCompleted;
import com.comp5348.bank.messaging.events.PaymentFailed;
import com.comp5348.bank.messaging.events.RefundCompleted;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usyd.comp5348.Topics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BankService {

    private final AccountRepository accountRepo;
    private final TransactionRecordRepository txRepo;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper om;  // ✅ 改用 Spring 管理的 ObjectMapper

    /** 扣款逻辑 */
    @Transactional
    public void pay(String orderNo, String fromAccountNo, String toAccountNo, BigDecimal amount) {
        var from = accountRepo.findById(Long.valueOf(fromAccountNo)).orElseThrow();
        var to = accountRepo.findById(Long.valueOf(toAccountNo)).orElseThrow();

        var tx = TransactionRecord.create(orderNo, from, to, amount);

        try {
            // 余额不足 → 支付失败
            if (from.getBalance().compareTo(amount) < 0) {
                tx.setStatus(TransactionRecord.Status.FAILED);
                txRepo.save(tx);

                saveEvent(orderNo, Topics.BankEvt.PAYMENT_FAILED,
                        PaymentFailed.of(orderNo, amount, "Insufficient balance"));
                return;
            }

            // 扣款并更新账户余额
            from.modifyBalance(amount.negate());
            to.modifyBalance(amount);
            accountRepo.save(from);
            accountRepo.save(to);

            // 保存交易记录
            tx.setStatus(TransactionRecord.Status.PAID);
            txRepo.save(tx);

            // 发布事件
            saveEvent(orderNo, Topics.BankEvt.PAID,
                    PaymentCompleted.of(orderNo, amount));

        } catch (Exception e) {
            tx.setStatus(TransactionRecord.Status.FAILED);
            txRepo.save(tx);
            saveEvent(orderNo, Topics.BankEvt.PAYMENT_FAILED,
                    PaymentFailed.of(orderNo, amount, e.getMessage()));
        }
    }

    /** 退款逻辑 */
    @Transactional
    public void refund(String orderNo) {
        var tx = txRepo.findByOrderNo(orderNo);
        if (tx == null) throw new RuntimeException("Transaction not found");

        var from = tx.getFromAccount();
        var to = tx.getToAccount();
        var amount = tx.getAmount();

        if (to.getBalance().compareTo(amount) < 0)
            throw new RuntimeException("Insufficient balance for refund");

        // 执行退款
        to.modifyBalance(amount.negate());
        from.modifyBalance(amount);
        accountRepo.save(from);
        accountRepo.save(to);

        tx.setStatus(TransactionRecord.Status.REFUNDED);
        txRepo.save(tx);

        saveEvent(orderNo, Topics.BankEvt.REFUNDED,
                RefundCompleted.of(orderNo, amount));
    }

    /** 查询支付状态 */
    public String getStatus(String orderNo) {
        var tx = txRepo.findByOrderNo(orderNo);
        return (tx != null) ? tx.getStatus().name() : "NOT_FOUND";
    }

    /** 写入 Outbox 表 */
    private void saveEvent(String orderNo, String type, Object event) {
        try {
            String payload = om.writeValueAsString(event);
            Instant now = Instant.now(); // ✅ 添加时间戳
            outboxRepo.save(Outbox.builder()
                    .aggregateId(orderNo)
                    .type(type)
                    .payload(payload)
                    .status(Outbox.Status.NEW)
                    .createdAt(now)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Outbox serialization failed", e);
        }
    }
}
