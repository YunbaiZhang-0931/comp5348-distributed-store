package com.comp5348.bank.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transaction_records")
public class TransactionRecord {

    public enum Status { PENDING, PAID, FAILED, REFUNDED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String orderNo;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Account fromAccount;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Account toAccount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    public static TransactionRecord create(String orderNo, Account from, Account to, BigDecimal amount) {
        return TransactionRecord.builder()
                .orderNo(orderNo)
                .fromAccount(from)
                .toAccount(to)
                .amount(amount)
                .dateTime(LocalDateTime.now())
                .status(Status.PENDING)
                .build();
    }
}

