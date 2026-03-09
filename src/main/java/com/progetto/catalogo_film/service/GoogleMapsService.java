package com.progetto.catalogo_film.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GoogleMapsService {

    private final WebClient webClient;

    @Value("${google.maps.api.key}")
    private String apiKey;

    public GoogleMapsService() {
        this.webClient = WebClient.create();
    }

    public Map<String, Object> trovaCinemaVicino(String citta) {
        String query = "cinema " + citta;
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json"
                + "?query=" + query.replace(" ", "+")
                + "&key=" + apiKey
                + "&language=it";

        Map response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null) return Map.of("errore", "Nessun risultato");

        List<Map> results = (List<Map>) response.get("results");
        if (results == null || results.isEmpty()) return Map.of("errore", "Nessun cinema trovato");

        Map primo = results.get(0);
        Map geometry = (Map) primo.get("geometry");
        Map location = (Map) geometry.get("location");

        return Map.of(
                "nome", primo.getOrDefault("name", ""),
                "indirizzo", primo.getOrDefault("formatted_address", ""),
                "lat", location.get("lat"),
                "lng", location.get("lng")
        );
    }
}