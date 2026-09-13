package edu.usyd.comp5348.store_api.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "customers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** 唯一用户名（演示账号：customer） */
    @Column(nullable = false, unique = true, length = 64)
    private String username;

    /** BCrypt 哈希（绝不存明文） */
    @Column(nullable = false, length = 100)
    private String passwordHash;

    /** 邮箱（email-svc 打印通知时会用到） */
    @Column(nullable = false, length = 128)
    private String email;
}
