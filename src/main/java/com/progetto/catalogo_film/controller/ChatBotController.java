package com.progetto.catalogo_film.controller;

import com.progetto.catalogo_film.service.ChatBotService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:4200") // Permette ad Angular di parlare con Java
public class ChatBotController {

    private final ChatBotService chatBotService;

    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @PostMapping
    public Map<String, String> getChatReply(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        String aiReply = chatBotService.generateReply(userMessage);
        return Map.of("reply", aiReply);
    }
}