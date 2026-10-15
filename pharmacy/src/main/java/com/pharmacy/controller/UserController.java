package com.pharmacy.controller;

import com.pharmacy.entity.Role;
import com.pharmacy.entity.User;
import com.pharmacy.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public UserController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", users.findAll());
        model.addAttribute("roles", Role.values());
        return "users";
    }

    @PostMapping("/create")
    public String create(@RequestParam String fullName, @RequestParam String email,
                         @RequestParam String password, @RequestParam Role role, RedirectAttributes ra) {
        String mail = email.trim().toLowerCase();
        if (fullName.isBlank() || mail.isBlank()) {
            ra.addFlashAttribute("error", "Name and email are required.");
        } else if (password.length() < 8) {
            ra.addFlashAttribute("error", "Password must be at least 8 characters.");
        } else if (users.findByEmail(mail).isPresent()) {
            ra.addFlashAttribute("error", "A user with this email already exists.");
        } else {
            User u = new User();
            u.setFullName(fullName.trim());
            u.setEmail(mail);
            u.setPassword(encoder.encode(password));
            u.setRole(role);
            u.setActive(true);
            users.save(u);
            ra.addFlashAttribute("success", "User created.");
        }
        return "redirect:/users";
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        users.findById(id).ifPresent(u -> {
            if (u.getEmail().equalsIgnoreCase(auth.getName())) {
                ra.addFlashAttribute("error", "You cannot deactivate your own account.");
            } else {
                u.setActive(!u.isActive());
                users.save(u);
                ra.addFlashAttribute("success", "User " + (u.isActive() ? "activated." : "deactivated."));
            }
        });
        return "redirect:/users";
    }
}
