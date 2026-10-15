package com.pharmacy.repository;

import com.pharmacy.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findByQuantityLessThanEqual(int quantity);

    List<Medicine> findByExpiryDateBefore(LocalDate date);

    long countByExpiryDateBefore(LocalDate date);

    @Query("select count(m) from Medicine m where m.quantity <= m.reorderLevel")
    long countLowStock();
}
