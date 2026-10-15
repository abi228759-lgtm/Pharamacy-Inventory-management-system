package com.pharmacy.controller;

import com.pharmacy.entity.Medicine;
import com.pharmacy.entity.Invoice;
import com.pharmacy.repository.InvoiceRepository;
import com.pharmacy.repository.MedicineRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Controller
public class HomeController {

    private final MedicineRepository repo;
    private final InvoiceRepository invoices;

    public HomeController(MedicineRepository repo, InvoiceRepository invoices) {
        this.repo = repo;
        this.invoices = invoices;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Medicine> all = repo.findAll();
        List<Medicine> expired = all.stream().filter(Medicine::isExpired)
                .sorted(Comparator.comparing(Medicine::getExpiryDate)).toList();
        List<Medicine> expiring = all.stream().filter(Medicine::isExpiringSoon)
                .sorted(Comparator.comparing(Medicine::getExpiryDate)).toList();
        List<Medicine> low = all.stream().filter(m -> m.isLowStock() && !m.isExpired())
                .sorted(Comparator.comparingInt(Medicine::getQuantity)).toList();
        BigDecimal value = all.stream()
                .map(m -> m.getPrice() == null ? BigDecimal.ZERO : m.getPrice().multiply(BigDecimal.valueOf(m.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Invoice> today = invoices.findByCreatedAtBetween(LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
        model.addAttribute("todayRevenue", today.stream().map(Invoice::getGrandTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        model.addAttribute("todayInvoices", today.size());
        model.addAttribute("total", all.size());
        model.addAttribute("expiredList", expired);
        model.addAttribute("expiringList", expiring);
        model.addAttribute("lowList", low);
        model.addAttribute("stockValue", value);
        return "home";
    }
}
