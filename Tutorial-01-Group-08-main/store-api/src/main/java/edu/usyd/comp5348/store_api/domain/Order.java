package edu.usyd.comp5348.store_api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order extends BaseEntity {

    public enum Status {
        CREATED,            // 刚创建，等待库存预留
        RESERVED,           // 库存预留成功
        PAID,               // 扣款成功
        DISPATCH_REQUESTED, // 已创建配送请求
        IN_DEPOT, ON_TRUCK, DELIVERED, // 配送阶段影子状态（便于前端查询）
        COMPLETED,          // 全流程成功
        CANCELLED;           // 已取消/失败补偿完成

        public boolean isCancelable() {
            return this == CREATED || this == RESERVED || this == PAID;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** 下单用户 */
    @Column(nullable = false)
    private String userId;

    /** 商品（一次只买一种） */
    @Column(nullable = false)
    private String itemId;

    /** 购买数量 */
    @Column(nullable = false)
    private int quantity;

    /** 订单总金额（unitPrice * quantity），下单时锁定金额 */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    /** 订单影子状态（供查询/演示） */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Status status;
}
