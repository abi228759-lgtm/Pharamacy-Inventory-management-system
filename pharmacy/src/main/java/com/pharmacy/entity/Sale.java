package com.pharmacy.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long medicineId;
    private String medicineName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal total;
    private String soldBy;
    private LocalDateTime soldAt;

    public String getSoldAtDisplay() {
        return soldAt == null ? "" : soldAt.format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
    }

    public Long getId() { return id; }
    public Long getMedicineId() { return medicineId; }
    public void setMedicineId(Long v) { this.medicineId = v; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String v) { this.medicineName = v; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int v) { this.quantity = v; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal v) { this.unitPrice = v; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal v) { this.total = v; }
    public String getSoldBy() { return soldBy; }
    public void setSoldBy(String v) { this.soldBy = v; }
    public LocalDateTime getSoldAt() { return soldAt; }
    public void setSoldAt(LocalDateTime v) { this.soldAt = v; }
}
