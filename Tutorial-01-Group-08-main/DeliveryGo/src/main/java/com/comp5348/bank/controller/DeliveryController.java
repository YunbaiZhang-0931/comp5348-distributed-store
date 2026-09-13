package com.comp5348.bank.controller;

import com.comp5348.bank.model.Shipment;
import com.comp5348.bank.repository.ShipmentRepository;
import com.comp5348.bank.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final ShipmentRepository shipmentRepo;

    /** 手动触发发货请求（调试用） */
    @PostMapping("/request")
    public ResponseEntity<?> request(@RequestParam String orderId, @RequestParam String warehouseId) {
        deliveryService.handleDeliveryRequest(orderId, warehouseId);
        return ResponseEntity.ok("Delivery started for order " + orderId);
    }

    /** 查询所有发货记录 */
    @GetMapping("/shipments")
    public List<Shipment> list() {
        return shipmentRepo.findAll();
    }
}
