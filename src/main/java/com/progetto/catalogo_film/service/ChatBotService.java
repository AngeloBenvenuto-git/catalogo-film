package com.progetto.catalogo_film.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import java.util.List;
import java.util.Map;

@Service
public class ChatBotService {

    private final WebClient webClient;

    public ChatBotService(@Value("${groq.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public Flux<String> generateStreamingReply(String userMessage) {
        Map<String, Object> body = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of("role", "system", "content", "Sei un assistente esperto di cinema. Rispondi in italiano."),
                        Map.of("role", "user", "content", userMessage)
                ),
                "stream", true
        );

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(String.class)
                .map(this::extractContentFromChunk)
                .filter(content -> !content.isEmpty());
    }

    private String extractContentFromChunk(String chunk) {
        if (chunk == null || chunk.contains("[DONE]")) return "";

        String jsonPart = chunk.startsWith("data: ") ? chunk.substring(6).trim() : chunk.trim();

        try {
            if (jsonPart.contains("\"content\":\"")) {
                String marker = "\"content\":\"";
                int start = jsonPart.indexOf(marker) + marker.length();
                int end = jsonPart.indexOf("\"", start);
                if (start > -1 && end > start) {
                    return jsonPart.substring(start, end)
                            .replace("\\n", "\n")
                            .replace("\\\"", "\"");
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }
}