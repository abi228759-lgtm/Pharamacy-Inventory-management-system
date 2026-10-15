package com.pharmacy.controller;

import com.pharmacy.entity.Feedback;
import com.pharmacy.repository.FeedbackRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackRepository repo;

    public FeedbackController(FeedbackRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String page(Model model, Authentication auth) {
        boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (admin) {
            List<Feedback> all = repo.findAllByOrderByCreatedAtDesc();
            model.addAttribute("feedbacks", all);
            model.addAttribute("average", all.isEmpty() ? "-" :
                    String.format("%.1f", all.stream().mapToInt(Feedback::getRating).average().orElse(0)));
        }
        return "feedback";
    }

    @PostMapping("/submit")
    public String submit(@RequestParam int rating, @RequestParam String message,
                         Authentication auth, RedirectAttributes ra) {
        if (rating < 1 || rating > 5 || message.isBlank()) {
            ra.addFlashAttribute("error", "Please give a rating (1-5) and a message.");
        } else {
            Feedback f = new Feedback();
            f.setUserName(auth.getName());
            f.setRating(rating);
            f.setMessage(message.trim().length() > 1000 ? message.trim().substring(0, 1000) : message.trim());
            repo.save(f);
            ra.addFlashAttribute("success", "Thank you for your feedback!");
        }
        return "redirect:/feedback";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/feedback";
    }
}
