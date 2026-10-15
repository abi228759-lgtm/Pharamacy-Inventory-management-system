package com.pharmacy.repository;

import com.pharmacy.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
