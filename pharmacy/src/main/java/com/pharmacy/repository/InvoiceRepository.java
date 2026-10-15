package com.pharmacy.repository;

import com.pharmacy.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findTop30ByOrderByCreatedAtDesc();

    List<Invoice> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}
