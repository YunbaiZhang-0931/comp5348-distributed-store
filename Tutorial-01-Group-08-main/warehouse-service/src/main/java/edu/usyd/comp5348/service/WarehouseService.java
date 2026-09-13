package edu.usyd.comp5348.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usyd.comp5348.Topics;
import edu.usyd.comp5348.domain.Outbox;
import edu.usyd.comp5348.domain.StockLevel;
import edu.usyd.comp5348.domain.StockReservation;
import edu.usyd.comp5348.dto.StockFinalized;
import edu.usyd.comp5348.dto.StockOutOfStock;
import edu.usyd.comp5348.dto.StockReleased;
import edu.usyd.comp5348.dto.StockReserved;
import edu.usyd.comp5348.repository.OutboxRepo;
import edu.usyd.comp5348.repository.StockLevelRepo;
import edu.usyd.comp5348.repository.StockReservationRepo;
import edu.usyd.comp5348.repository.WarehouseRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepo warehouseRepo;
    private final StockLevelRepo stockRepo;
    private final StockReservationRepo reservationRepo;
    private final OutboxRepo outboxRepo;
    private final ObjectMapper om;

    @Transactional
    public void reserve(String orderId, String itemId, int qty, BigDecimal amount) {
        var stockList = stockRepo.findByItemId(itemId);
        int remaining = qty;

        for (StockLevel s : stockList) {
            if (remaining <= 0) break;

            int take = Math.min(s.getQuantity(), remaining);
            if (take > 0) {
                s.setQuantity(s.getQuantity() - take);
                stockRepo.save(s);

                reservationRepo.save(StockReservation.builder()
                        .orderId(orderId)
                        .warehouse(s.getWarehouse())
                        .itemId(itemId)
                        .quantity(take)
                        .status(StockReservation.Status.RESERVED)
                        .build());

                remaining -= take;
            }
        }

        if (remaining > 0) {
            log.warn("Not enough stock for item {}", itemId);
            throw new RuntimeException("Not enough stock");
        }

        publishEvent(orderId, itemId, qty, Topics.WarehouseEvt.RESERVED, amount);

    }

    public void publishOutOfStock(String orderId, String itemId, int qty, BigDecimal amount) {
        publishEvent(orderId, itemId, qty, Topics.WarehouseEvt.OUT_OF_STOCK, amount);
    }

    @Transactional
    public void check(String orderId, String itemId, int qty, BigDecimal amount) {
        var stockList = stockRepo.findByItemId(itemId);
        int stockLevel = 0;
        for (StockLevel s : stockList) {
            stockLevel += s.getQuantity();
        }
        if (stockLevel < qty) {
            publishEvent(orderId, itemId, qty, Topics.WarehouseEvt.OUT_OF_STOCK, amount);
        } else {
//            publishEvent(orderId, itemId, qty, Topics., amount);
        }
    }


    @Transactional
    public void release(String orderId) {
        var list = reservationRepo.findByOrderId(orderId);
        for (var r : list) {
            if (r.getStatus() == StockReservation.Status.RESERVED) {
                var level = stockRepo.findByItemId(r.getItemId()).stream()
                        .filter(l -> l.getWarehouse().getId().equals(r.getWarehouse().getId()))
                        .findFirst().orElseThrow();
                level.setQuantity(level.getQuantity() + r.getQuantity());
                stockRepo.save(level);
                r.setStatus(StockReservation.Status.RELEASED);
                reservationRepo.save(r);
            }
        }
        publishEvent(orderId, null, 0, "order.released", BigDecimal.ZERO);
    }

    @Transactional
    public void finalizeReservation(String orderId) {
        var list = reservationRepo.findByOrderId(orderId);
        for (var r : list) {
            r.setStatus(StockReservation.Status.FINALIZED);
            reservationRepo.save(r);
        }
        publishEvent(orderId, null, 0, "order.finalized", BigDecimal.ZERO);
    }

    private void publishEvent(String orderId, String itemId, int qty, String type, BigDecimal amount) {
        try {
            String payload;

            switch (type) {
                case Topics.WarehouseEvt.RESERVED ->
                        payload = om.writeValueAsString(StockReserved.of(orderId, itemId, qty, amount));
                case Topics.WarehouseEvt.RELEASED -> payload = om.writeValueAsString(StockReleased.of(orderId));
                case Topics.WarehouseEvt.FINALIZED -> payload = om.writeValueAsString(StockFinalized.of(orderId));
                case Topics.WarehouseEvt.OUT_OF_STOCK ->
                        payload = om.writeValueAsString(StockOutOfStock.of(orderId, itemId, qty, amount));
                default -> throw new IllegalArgumentException("Unknown event type: " + type);
            }

            outboxRepo.save(Outbox.builder()
                    .aggregateId(orderId)
                    .type(type)
                    .payload(payload)
                    .status(Outbox.Status.NEW)
                    .build());

        } catch (Exception e) {
            throw new RuntimeException("Failed to write outbox event: " + e.getMessage(), e);
        }
    }


}
