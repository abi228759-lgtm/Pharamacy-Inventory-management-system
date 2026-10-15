package com.pharmacy.service;

import com.pharmacy.entity.Medicine;
import com.pharmacy.repository.MedicineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class MedicineService {

    private final MedicineRepository repo;

    public MedicineService(MedicineRepository repo) {
        this.repo = repo;
    }

    public List<Medicine> findAll() {
        return repo.findAll();
    }

    public Medicine get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Medicine " + id + " not found"));
    }

    @Transactional
    public Medicine create(Medicine m) {
        m.setId(null);
        return repo.save(m);
    }

    @Transactional
    public Medicine update(Long id, Medicine m) {
        get(id);
        m.setId(id);
        return repo.save(m);
    }

    @Transactional
    public void delete(Long id) {
        get(id);
        repo.deleteById(id);
    }

    public Map<String, Object> summary() {
        List<Medicine> all = repo.findAll();
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("total", all.size());
        s.put("expired", all.stream().filter(Medicine::isExpired).count());
        s.put("expiringSoon", all.stream().filter(Medicine::isExpiringSoon).count());
        s.put("lowStock", all.stream().filter(m -> m.isLowStock() && !m.isExpired()).count());
        s.put("stockValue", all.stream()
                .map(m -> m.getPrice() == null ? BigDecimal.ZERO : m.getPrice().multiply(BigDecimal.valueOf(m.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return s;
    }
}
