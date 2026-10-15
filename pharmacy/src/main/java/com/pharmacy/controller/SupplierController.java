package com.pharmacy.controller;

import com.pharmacy.entity.Supplier;
import com.pharmacy.repository.SupplierRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierRepository repo;

    public SupplierController(SupplierRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("suppliers", repo.findAll());
        return "suppliers";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Supplier supplier, RedirectAttributes ra) {
        if (supplier.getName() == null || supplier.getName().isBlank()) {
            ra.addFlashAttribute("error", "Supplier name is required.");
        } else {
            repo.save(supplier);
            ra.addFlashAttribute("success", "Supplier saved.");
        }
        return "redirect:/suppliers";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        repo.deleteById(id);
        ra.addFlashAttribute("success", "Supplier deleted.");
        return "redirect:/suppliers";
    }
}
