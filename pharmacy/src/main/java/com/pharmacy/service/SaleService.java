package com.pharmacy.service;

import com.pharmacy.entity.Medicine;
import com.pharmacy.entity.Sale;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SaleService {

    private final MedicineRepository medicines;
    private final SaleRepository sales;

    public SaleService(MedicineRepository medicines, SaleRepository sales) {
        this.medicines = medicines;
        this.sales = sales;
    }

    @Transactional
    public Sale sell(Long medicineId, int quantity, String user) {
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be at least 1");
        Medicine m = medicines.findById(medicineId)
                .orElseThrow(() -> new NoSuchElementException("Medicine " + medicineId + " not found"));
        if (m.isExpired()) throw new IllegalArgumentException("Cannot sell an expired medicine");
        if (quantity > m.getQuantity()) throw new IllegalArgumentException("Only " + m.getQuantity() + " in stock");

        m.setQuantity(m.getQuantity() - quantity);
        medicines.save(m);

        Sale s = new Sale();
        s.setMedicineId(m.getId());
        s.setMedicineName(m.getName());
        s.setQuantity(quantity);
        s.setUnitPrice(m.getPrice());
        s.setTotal(m.getPrice().multiply(BigDecimal.valueOf(quantity)));
        s.setSoldBy(user);
        s.setSoldAt(LocalDateTime.now());
        return sales.save(s);
    }

    public List<Sale> recent() {
        return sales.findTop50ByOrderBySoldAtDesc();
    }
}
