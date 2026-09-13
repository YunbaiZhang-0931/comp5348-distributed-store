package edu.usyd.comp5348.store_api.web;

import edu.usyd.comp5348.store_api.domain.Order;
import edu.usyd.comp5348.store_api.repo.ItemRepo;
import edu.usyd.comp5348.store_api.repo.OrderRepo;
import edu.usyd.comp5348.store_api.service.OrderService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderRepo orderRepo;
    private final ItemRepo itemRepo;

    public OrderController(OrderService orderService, OrderRepo orderRepo, ItemRepo itemRepo) {
        this.orderService = orderService;
        this.orderRepo = orderRepo;
        this.itemRepo = itemRepo;
    }

    /** 创建订单（一次只买一种商品的数量） */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateResp create(@RequestBody CreateReq req) {
        var item = itemRepo.findById(req.getItemId()).orElseThrow();
        var o = orderService.create(req.getUserId(), req.getItemId(), req.getQuantity(), item.getUnitPrice());
        return new CreateResp(o.getId(), o.getStatus().name(), o.getAmount());
    }

    /** 查询订单 */
    @GetMapping("/{orderId}")
    public Order get(@PathVariable String orderId) {
        return orderRepo.findById(orderId).orElseThrow();
    }

    /** 取消订单（仅在发货请求前允许） */
    @PostMapping("/{orderId}/cancel")
    public CancelResp cancel(@PathVariable String orderId) {
        orderService.cancel(orderId);
        return new CancelResp(true, "cancel requested");
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam("status") String status) {
        orderService.updateOrderStatus(orderId, Order.Status.valueOf(status));
        return ResponseEntity.ok("Order status updated to " + status);
    }


    /** 方便演示：列出可购买的商品 */
    @GetMapping("/items")
    public Object items() {
        return itemRepo.findByEnabledTrueOrderByNameAsc();
    }

    @Data
    public static class CreateReq {
        private String userId;
        private String itemId;
        private int quantity;
    }
    public record CreateResp(String orderId, String status, BigDecimal amount){}
    public record CancelResp(boolean ok, String msg){}
}
