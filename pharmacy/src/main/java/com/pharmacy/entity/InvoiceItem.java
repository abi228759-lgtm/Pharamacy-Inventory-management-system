package com.pharmacy.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class InvoiceItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String medicineName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public Long getId() { return id; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String v) { this.medicineName = v; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int v) { this.quantity = v; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal v) { this.unitPrice = v; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal v) { this.lineTotal = v; }
}
