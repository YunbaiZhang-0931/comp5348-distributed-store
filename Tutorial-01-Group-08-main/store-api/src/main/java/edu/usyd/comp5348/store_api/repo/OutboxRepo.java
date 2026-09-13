package edu.usyd.comp5348.store_api.repo;

import edu.usyd.comp5348.store_api.domain.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** 事务消息表：定时发布器会扫描 NEW 状态的记录并发送到 MQ */
public interface OutboxRepo extends JpaRepository<Outbox, String> {
    List<Outbox> findTop100ByStatusOrderByCreatedAtAsc(Outbox.Status status);
}
