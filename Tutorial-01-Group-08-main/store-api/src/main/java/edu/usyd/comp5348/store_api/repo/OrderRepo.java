package edu.usyd.comp5348.store_api.repo;

import edu.usyd.comp5348.store_api.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** 订单表：查询用户最近订单/按状态查询等 */
public interface OrderRepo extends JpaRepository<Order, String> {
    List<Order> findTop20ByUserIdOrderByCreatedAtDesc(String userId);
    List<Order> findByStatusOrderByCreatedAtDesc(Order.Status status);
}
