package edu.usyd.comp5348.repository;

import edu.usyd.comp5348.domain.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepo extends JpaRepository<Warehouse, String> {}