package edu.usyd.comp5348.store_api.domain;

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

    /** 聚合根ID，一般用 orderId，便于排错与追踪 */
    @Column(nullable = false)
    private String aggregateId;

    /** 事件类型（如：order.created / order.cancel.requested） */
    @Column(nullable = false, length = 64)
    private String type;

    /** 事件 JSON（序列化后的 DTO） */
    @Lob
    @Column(nullable = false)
    private String payload;

    /** 发送状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;
}
