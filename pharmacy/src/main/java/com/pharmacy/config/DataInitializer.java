package com.pharmacy.config;

import com.pharmacy.entity.Medicine;
import com.pharmacy.entity.Role;
import com.pharmacy.repository.MedicineRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.pharmacy.entity.User;
import com.pharmacy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner createAdminUser(UserRepository userRepository,
                                      PasswordEncoder passwordEncoder,
                                      @Value("${app.admin.email}") String email,
                                      @Value("${app.admin.password}") String password) {
        return args -> {
            if (userRepository.findByEmail(email).isEmpty()) {
                User admin = new User();
                admin.setFullName("Pharmacy Administrator");
                admin.setEmail(email);
                admin.setPassword(passwordEncoder.encode(password));
                admin.setRole(Role.ADMIN);
                admin.setActive(true);
                userRepository.save(admin);
            }
        };
    }

    @Bean
    CommandLineRunner seedMedicines(MedicineRepository repo) {
        return args -> {
            if (repo.count() > 0) return;
            Object[][] d = {
                {"Paracetamol 500mg", "Painkiller", "PCM-101", 120, 30, 200, "12.50"},
                {"Amoxicillin 250mg", "Antibiotic", "AMX-220", 8, 20, 12, "45.00"},
                {"Cetirizine 10mg", "Antihistamine", "CTZ-310", 60, 15, -5, "18.00"},
                {"Azithromycin 500mg", "Antibiotic", "AZT-415", 40, 15, 18, "95.75"},
                {"Metformin 500mg", "Diabetes", "MET-512", 5, 25, 300, "22.00"}
            };
            for (Object[] r : d) {
                Medicine m = new Medicine();
                m.setName((String) r[0]); m.setCategory((String) r[1]); m.setBatchNumber((String) r[2]);
                m.setQuantity((Integer) r[3]); m.setReorderLevel((Integer) r[4]);
                m.setExpiryDate(LocalDate.now().plusDays((Integer) r[5]));
                m.setPrice(new BigDecimal((String) r[6]));
                repo.save(m);
            }
        };
    }
}
