package com.pharmacy.api;

import com.pharmacy.entity.Medicine;
import com.pharmacy.service.MedicineService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medicines")
public class MedicineApiController {

    private final MedicineService service;

    public MedicineApiController(MedicineService service) {
        this.service = service;
    }

    @GetMapping
    public List<Medicine> all() { return service.findAll(); }

    @GetMapping("/summary")
    public Map<String, Object> summary() { return service.summary(); }

    @GetMapping("/{id}")
    public Medicine one(@PathVariable Long id) { return service.get(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Medicine create(@Valid @RequestBody Medicine m) { return service.create(m); }

    @PutMapping("/{id}")
    public Medicine update(@PathVariable Long id, @Valid @RequestBody Medicine m) { return service.update(id, m); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
