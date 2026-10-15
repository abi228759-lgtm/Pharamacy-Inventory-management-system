package com.pharmacy.controller;

import com.pharmacy.service.ChatbotService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    public record ChatRequest(String message) {}
    public record ChatReply(String reply) {}

    private final ChatbotService service;

    public ChatbotController(ChatbotService service) {
        this.service = service;
    }

    @PostMapping("/ask")
    public ChatReply ask(@RequestBody ChatRequest request) {
        return new ChatReply(service.reply(request.message()));
    }
}
