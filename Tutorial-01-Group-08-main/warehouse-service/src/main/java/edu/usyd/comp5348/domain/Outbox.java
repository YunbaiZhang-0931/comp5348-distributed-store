package edu.usyd.comp5348.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "outbox")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Outbox extends BaseEntity {

    public enum Status { NEW, SENT }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** 聚合根ID，比如 orderId 或 reservationId */
    @Column(nullable = false, length = 64)
    private String aggregateId;

    /** 事件类型，如 order.reserved, order.released */
    @Column(nullable = false, length = 64)
    private String type;

    /** 序列化的 JSON 事件内容 */
    @Lob
    @Column(nullable = false)
    private String payload;

    /** 状态（未发送 / 已发送） */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;
}
