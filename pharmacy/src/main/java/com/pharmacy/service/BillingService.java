package com.pharmacy.service;

import com.pharmacy.entity.Invoice;
import com.pharmacy.entity.InvoiceItem;
import com.pharmacy.entity.Medicine;
import com.pharmacy.repository.InvoiceRepository;
import com.pharmacy.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BillingService {

    private final MedicineRepository medicines;
    private final InvoiceRepository invoices;
    private final BigDecimal gstPercent;

    public BillingService(MedicineRepository medicines, InvoiceRepository invoices,
                          @Value("${app.billing.gst-percent:12}") BigDecimal gstPercent) {
        this.medicines = medicines;
        this.invoices = invoices;
        this.gstPercent = gstPercent;
    }

    @Transactional
    public Invoice create(String customer, String phone, BigDecimal discountPercent,
                          List<Long> ids, List<Integer> quantities, String user) {
        if (ids == null || ids.isEmpty() || ids.size() != quantities.size())
            throw new IllegalArgumentException("Add at least one item to the bill");
        BigDecimal disc = discountPercent == null ? BigDecimal.ZERO : discountPercent.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100));

        Invoice inv = new Invoice();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (int i = 0; i < ids.size(); i++) {
            int qty = quantities.get(i);
            Medicine m = medicines.findById(ids.get(i))
                    .orElseThrow(() -> new NoSuchElementException("Medicine not found"));
            if (qty < 1) throw new IllegalArgumentException("Quantity must be at least 1");
            if (m.isExpired()) throw new IllegalArgumentException(m.getName() + " is expired and cannot be sold");
            if (qty > m.getQuantity()) throw new IllegalArgumentException("Only " + m.getQuantity() + " of " + m.getName() + " in stock");

            m.setQuantity(m.getQuantity() - qty);
            medicines.save(m);

            InvoiceItem it = new InvoiceItem();
            it.setMedicineName(m.getName());
            it.setQuantity(qty);
            it.setUnitPrice(m.getPrice());
            it.setLineTotal(m.getPrice().multiply(BigDecimal.valueOf(qty)));
            inv.getItems().add(it);
            subtotal = subtotal.add(it.getLineTotal());
        }
        BigDecimal discAmt = subtotal.multiply(disc).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal taxable = subtotal.subtract(discAmt);
        BigDecimal gst = taxable.multiply(gstPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        inv.setCustomerName(customer == null || customer.isBlank() ? "Walk-in customer" : customer.trim());
        inv.setCustomerPhone(phone);
        inv.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        inv.setDiscountPercent(disc);
        inv.setDiscountAmount(discAmt);
        inv.setGstPercent(gstPercent);
        inv.setGstAmount(gst);
        inv.setGrandTotal(taxable.add(gst).setScale(2, RoundingMode.HALF_UP));
        inv.setCreatedBy(user);
        inv.setCreatedAt(LocalDateTime.now());
        invoices.save(inv);
        inv.setInvoiceNumber("INV-" + String.format("%05d", inv.getId()));
        return invoices.save(inv);
    }

    public Invoice get(Long id) {
        return invoices.findById(id).orElseThrow(() -> new NoSuchElementException("Invoice not found"));
    }

    public List<Invoice> recent() {
        return invoices.findTop30ByOrderByCreatedAtDesc();
    }
}
