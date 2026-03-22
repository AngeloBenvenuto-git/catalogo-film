package com.progetto.catalogo_film.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class ChatBotService {

    private final WebClient webClient;

    public ChatBotService(@Value("${groq.api.key}") String apiKey) {

        System.out.println("API KEY: " + apiKey); // DEBUG

        this.webClient = WebClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String generateReply(String userMessage) {

        Map<String, Object> body = Map.of(
                "model", "llama-3.1-8b-instant",
                "max_tokens", 200,
                "temperature", 0.7,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", "Rispondi sempre in italiano. Sei un assistente utile per un sito di film."
                        ),
                        Map.of(
                                "role", "user",
                                "content", userMessage
                        )
                )
        );

        try {
            Map response = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        System.out.println("ERRORE API: " + errorBody);
                                        return Mono.error(new RuntimeException("Errore API: " + errorBody));
                                    })
                    )

                    .bodyToMono(Map.class)
                    .block();

            // DEBUG: stampa risposta completa
            System.out.println("RESPONSE: " + response);

            if (response == null || !response.containsKey("choices")) {
                return "Errore: risposta non valida dall'API";
            }

            List choices = (List) response.get("choices");
            if (choices.isEmpty()) {
                return "Errore: nessuna risposta generata";
            }

            Map firstChoice = (Map) choices.get(0);
            Map messageObj = (Map) firstChoice.get("message");

            return (String) messageObj.get("content");

        } catch (Exception e) {
            e.printStackTrace();
            return "Errore durante la chiamata al chatbot: " + e.getMessage();
        }
    }
}
