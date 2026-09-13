package edu.usyd.comp5348.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name="outbox")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Outbox extends BaseEntity {
    public enum Status { NEW, SENT }
    @Id @GeneratedValue(strategy=GenerationType.UUID) private String id;
    @Column(nullable=false) private String aggregateId;   // e.g. orderId
    @Column(nullable=false, length=64) private String type; // routing key
    @Lob @Column(nullable=false) private String payload;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=16) private Status status;
}