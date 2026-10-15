package com.pharmacy.service;

import com.pharmacy.entity.Invoice;
import com.pharmacy.entity.Medicine;
import com.pharmacy.repository.InvoiceRepository;
import com.pharmacy.repository.MedicineRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/** Rule-based assistant that answers from live database data (no external AI service needed). */
@Service
public class ChatbotService {

    private final MedicineRepository medicines;
    private final InvoiceRepository invoices;

    public ChatbotService(MedicineRepository medicines, InvoiceRepository invoices) {
        this.medicines = medicines;
        this.invoices = invoices;
    }

    public String reply(String message) {
        String m = message == null ? "" : message.toLowerCase().trim();
        if (m.isEmpty()) return "Please type a question. Say \"help\" to see what I can do.";
        List<Medicine> all = medicines.findAll();

        for (Medicine med : all) {
            String key = med.getName().toLowerCase().split("\\s+")[0];
            if (m.contains(key)) return describe(med);
        }
        if (m.matches(".*\\b(help|menu|options)\\b.*"))
            return "You can ask me:\n- \"expired medicines\"\n- \"expiring soon\"\n- \"low stock\"\n- \"stock summary\"\n- \"today's sales\"\n- or a medicine name, e.g. \"price of paracetamol\"";
        if (m.matches(".*\\b(hi|hello|hey)\\b.*")) return "Hello! I'm your pharmacy assistant. Type \"help\" to see what I can do.";
        if (m.contains("expired")) return list("Expired medicines", all, Medicine::isExpired);
        if (m.contains("expiring") || m.contains("expire") || m.contains("expiry"))
            return list("Expiring within 30 days", all, Medicine::isExpiringSoon);
        if (m.contains("low") || m.contains("reorder") || m.contains("out of stock"))
            return list("Low stock", all, x -> x.isLowStock() && !x.isExpired());
        if (m.contains("summary") || m.contains("total") || m.contains("value")) {
            BigDecimal value = all.stream().map(x -> x.getPrice().multiply(BigDecimal.valueOf(x.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return "Medicines: " + all.size() + "\nExpired: " + all.stream().filter(Medicine::isExpired).count()
                    + "\nExpiring soon: " + all.stream().filter(Medicine::isExpiringSoon).count()
                    + "\nLow stock: " + all.stream().filter(x -> x.isLowStock() && !x.isExpired()).count()
                    + "\nStock value: Rs. " + value;
        }
        if (m.contains("sale") || m.contains("revenue") || m.contains("bill") || m.contains("today")) {
            List<Invoice> today = invoices.findByCreatedAtBetween(LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            BigDecimal sum = today.stream().map(Invoice::getGrandTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            return "Today: " + today.size() + " bill(s), revenue Rs. " + sum;
        }
        return "Sorry, I didn't understand that. Type \"help\" to see what I can answer.";
    }

    private String describe(Medicine x) {
        String status = x.isExpired() ? "EXPIRED" : x.isExpiringSoon() ? "Expiring in " + x.getDaysToExpiry() + " days" : x.isLowStock() ? "Low stock" : "OK";
        return x.getName() + "\nCategory: " + x.getCategory() + "\nStock: " + x.getQuantity() + "\nPrice: Rs. " + x.getPrice()
                + "\nExpiry: " + x.getExpiryDate() + "\nStatus: " + status;
    }

    private String list(String title, List<Medicine> all, Predicate<Medicine> p) {
        String body = all.stream().filter(p)
                .map(x -> "- " + x.getName() + " (qty " + x.getQuantity() + ", expires " + x.getExpiryDate() + ")")
                .collect(Collectors.joining("\n"));
        return body.isEmpty() ? title + ": none. All good!" : title + ":\n" + body;
    }
}
