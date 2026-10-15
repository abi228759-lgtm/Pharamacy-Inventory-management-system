package com.pharmacy.controller;

import com.pharmacy.entity.Medicine;
import com.pharmacy.repository.MedicineRepository;
import com.pharmacy.service.SaleService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/sales")
public class SalesController {

    private final SaleService saleService;
    private final MedicineRepository medicines;

    public SalesController(SaleService saleService, MedicineRepository medicines) {
        this.saleService = saleService;
        this.medicines = medicines;
    }

    @GetMapping
    public String page(Model model) {
        model.addAttribute("available", medicines.findAll().stream()
                .filter(m -> !m.isExpired() && m.getQuantity() > 0).toList());
        model.addAttribute("sales", saleService.recent());
        return "sales";
    }

    @PostMapping("/sell")
    public String sell(@RequestParam Long medicineId, @RequestParam int quantity,
                       Authentication auth, RedirectAttributes ra) {
        try {
            var s = saleService.sell(medicineId, quantity, auth.getName());
            ra.addFlashAttribute("success", "Sold " + s.getQuantity() + " x " + s.getMedicineName() + " for Rs. " + s.getTotal());
        } catch (IllegalArgumentException | NoSuchElementException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/sales";
    }
}
