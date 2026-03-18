package com.progetto.catalogo_film.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Service
public class ChatBotService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.create();

    public String generateReply(String message) {
        // URL per il modello 1.5-flash (il più veloce e stabile per i test)
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        // Struttura JSON minima richiesta da Google
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", "Rispondi come assistente del sito NetFilm. Utente: " + message)
                        ))
                )
        );

        try {
            Map response = webClient.post()
                    .uri(url)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // Estrazione del testo dalla "matrioska" di Google
            if (response != null && response.get("candidates") != null) {
                List candidates = (List) response.get("candidates");
                Map candidate = (Map) candidates.get(0);
                Map content = (Map) candidate.get("content");
                List parts = (List) content.get("parts");
                return (String) ((Map) parts.get(0)).get("text");
            }
        } catch (Exception e) {
            // Se fallisce, stampa l'errore REALE nella console di IntelliJ
            System.err.println("ERRORE API GEMINI: " + e.getMessage());
        }

        return "Scusa, sto ricaricando le mie conoscenze. Riprova tra un istante! 🍿";
    }
}