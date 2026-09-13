package edu.usyd.comp5348.store_api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 120)
    private String name;

    /** 含税单价（下单时用于计算订单金额） */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    /** 是否上架（卖合法的实体商品） */
    @Column(nullable = false)
    private boolean enabled;
}
