package edu.usyd.comp5348.controller;

import edu.usyd.comp5348.dto.FinalizeRequest;
import edu.usyd.comp5348.dto.ReleaseRequest;
import edu.usyd.comp5348.dto.ReserveRequest;
import edu.usyd.comp5348.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService service;

    @PostMapping("/reserve")
    public ResponseEntity<String> reserve(@RequestBody ReserveRequest req) {
        service.reserve(req.getOrderId(), req.getItemId(), req.getQuantity(), BigDecimal.ZERO);
        return ResponseEntity.ok("Reserved");
    }

    @PostMapping("/release")
    public ResponseEntity<String> release(@RequestBody ReleaseRequest req) {
        service.release(req.getOrderId());
        return ResponseEntity.ok("Released");
    }

    @PostMapping("/finalize")
    public ResponseEntity<String> finalize(@RequestBody FinalizeRequest req) {
        service.finalizeReservation(req.getOrderId());
        return ResponseEntity.ok("Finalized");
    }
}
