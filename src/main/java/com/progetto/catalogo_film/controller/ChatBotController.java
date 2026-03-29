package com.progetto.catalogo_film.controller;

import com.progetto.catalogo_film.service.ChatBotService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatBotController {

    private final ChatBotService chatBotService;

    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getChatStream(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        return chatBotService.generateStreamingReply(message);
    }
}