package com.comp5348.bank.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "outbox")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Outbox extends BaseEntity {

    public enum Status { NEW, SENT }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** 聚合根 ID（例如 orderId） */
    @Column(nullable = false)
    private String aggregateId;

    /** 事件类型，例如 delivery.received */
    @Column(nullable = false, length = 64)
    private String type;

    /** JSON 事件数据 */
    @Lob
    @Column(nullable = false)
    private String payload;

    /** 状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;
}
