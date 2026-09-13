package com.comp5348.bank.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "outbox")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Outbox {

    public enum Status { NEW, SENT }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** 聚合根ID，比如订单号 orderNo */
    @Column(nullable = false, length = 64)
    private String aggregateId;

    /** 事件类型，例如 order.paid, order.payment_failed, order.refunded */
    @Column(nullable = false, length = 64)
    private String type;

    /** 事件内容（JSON） */
    @Lob
    @Column(nullable = false)
    private String payload;

    /** 发送状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    /** 创建时间戳 */
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
