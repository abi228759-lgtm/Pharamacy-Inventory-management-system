package com.pharmacy.controller;

import com.pharmacy.entity.Medicine;
import com.pharmacy.repository.MedicineRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/medicines")
public class MedicineController {

    private final MedicineRepository repo;

    public MedicineController(MedicineRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String status, Model model) {
        model.addAttribute("medicines", filter(q, status));
        model.addAttribute("medicine", new Medicine());
        model.addAttribute("q", q);
        model.addAttribute("status", status);
        return "medicines";
    }

    @GetMapping("/export")
    public ResponseEntity<String> export(@RequestParam(required = false) String q,
                                         @RequestParam(required = false) String status) {
        StringBuilder sb = new StringBuilder("ID,Name,Category,Batch,Quantity,Reorder Level,Expiry,Days Left,Price\n");
        for (Medicine m : filter(q, status)) {
            sb.append(m.getId()).append(',').append(csv(m.getName())).append(',').append(csv(m.getCategory())).append(',')
              .append(csv(m.getBatchNumber())).append(',').append(m.getQuantity()).append(',').append(m.getReorderLevel()).append(',')
              .append(m.getExpiryDate()).append(',').append(m.getDaysToExpiry()).append(',').append(m.getPrice()).append('\n');
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=medicines.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(sb.toString());
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return repo.findById(id).map(m -> {
            model.addAttribute("medicines", filter(null, null));
            model.addAttribute("medicine", m);
            return "medicines";
        }).orElseGet(() -> {
            ra.addFlashAttribute("error", "Medicine not found.");
            return "redirect:/medicines";
        });
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("medicine") Medicine medicine,
                       BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("medicines", filter(null, null));
            return "medicines";
        }
        repo.save(medicine);
        ra.addFlashAttribute("success", "Medicine saved successfully.");
        return "redirect:/medicines";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        repo.deleteById(id);
        ra.addFlashAttribute("success", "Medicine deleted.");
        return "redirect:/medicines";
    }

    private List<Medicine> filter(String q, String status) {
        String needle = q == null ? "" : q.trim().toLowerCase();
        return repo.findAll().stream()
                .filter(m -> needle.isEmpty() || has(m.getName(), needle) || has(m.getCategory(), needle) || has(m.getBatchNumber(), needle))
                .filter(m -> status == null || status.isBlank() || switch (status) {
                    case "EXPIRED" -> m.isExpired();
                    case "EXPIRING" -> m.isExpiringSoon();
                    case "LOW" -> m.isLowStock();
                    default -> true;
                })
                .sorted(Comparator.comparing(Medicine::getExpiryDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    private boolean has(String v, String needle) {
        return v != null && v.toLowerCase().contains(needle);
    }

    private String csv(String v) {
        return v == null ? "" : "\"" + v.replace("\"", "\"\"") + "\"";
    }
}
