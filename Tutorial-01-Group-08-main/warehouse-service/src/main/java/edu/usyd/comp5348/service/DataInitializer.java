package edu.usyd.comp5348.service;

import edu.usyd.comp5348.domain.StockLevel;
import edu.usyd.comp5348.domain.Warehouse;
import edu.usyd.comp5348.repository.StockLevelRepo;
import edu.usyd.comp5348.repository.WarehouseRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final WarehouseRepo warehouseRepo;
    private final StockLevelRepo stockRepo;

    /**
     * 应用启动后执行一次：初始化仓库与库存
     */
    @PostConstruct
    public void initData() {
        if (warehouseRepo.count() == 0) {
            // 创建两个仓库
            Warehouse sydney = new Warehouse();
            sydney.setName("Sydney Central Warehouse");
            sydney.setLocation("Sydney NSW");

            Warehouse melbourne = new Warehouse();
            melbourne.setName("Melbourne Distribution Center");
            melbourne.setLocation("Melbourne VIC");

            warehouseRepo.saveAll(List.of(sydney, melbourne));

            // 为每个仓库添加三个商品库存（使用固定 itemId）
            // 例如 ITEM-001, ITEM-002, ITEM-003
            List<String> itemIds = List.of("ITEM-001", "ITEM-002", "ITEM-003");

            for (Warehouse wh : List.of(sydney, melbourne)) {
                for (String itemId : itemIds) {
                    StockLevel stock = new StockLevel();
                    stock.setWarehouse(wh);
                    stock.setItemId(itemId);
                    stock.setQuantity((int) (Math.random() * 50 + 10)); // 随机 10–60 件
                    stockRepo.save(stock);
                }
            }

            System.out.println("Initialized warehouses and stock levels.");
        } else {
            System.out.println("Warehouses already exist, skipping initialization.");
        }
    }
}
