package com.pharmacy.controller;

import com.pharmacy.entity.Invoice;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.service.BillingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/billing")
public class BillingController {

    private final BillingService billing;
    private final MedicineRepository medicines;

    public BillingController(BillingService billing, MedicineRepository medicines) {
        this.billing = billing;
        this.medicines = medicines;
    }

    @GetMapping
    public String page(Model model) {
        model.addAttribute("available", medicines.findAll().stream().filter(m -> !m.isExpired() && m.getQuantity() > 0).toList());
        model.addAttribute("invoices", billing.recent());
        return "billing";
    }

    @PostMapping("/create")
    public String create(@RequestParam(required = false) String customerName,
                         @RequestParam(required = false) String customerPhone,
                         @RequestParam(defaultValue = "0") BigDecimal discountPercent,
                         @RequestParam List<Long> medicineId, @RequestParam List<Integer> quantity,
                         Authentication auth, RedirectAttributes ra) {
        try {
            Invoice inv = billing.create(customerName, customerPhone, discountPercent, medicineId, quantity, auth.getName());
            return "redirect:/billing/" + inv.getId();
        } catch (IllegalArgumentException | NoSuchElementException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/billing";
        }
    }

    @GetMapping("/{id}")
    public String invoice(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("invoice", billing.get(id));
            return "invoice";
        } catch (NoSuchElementException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/billing";
        }
    }
}
