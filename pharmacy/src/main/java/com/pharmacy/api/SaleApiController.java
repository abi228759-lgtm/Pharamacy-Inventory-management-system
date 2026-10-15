package com.pharmacy.api;

import com.pharmacy.entity.Sale;
import com.pharmacy.service.SaleService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleApiController {

    public record SaleRequest(@NotNull Long medicineId, @Min(1) int quantity) {}

    private final SaleService service;

    public SaleApiController(SaleService service) {
        this.service = service;
    }

    @GetMapping
    public List<Sale> recent() { return service.recent(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sale sell(@Valid @RequestBody SaleRequest r, Authentication auth) {
        return service.sell(r.medicineId(), r.quantity(), auth.getName());
    }
}
