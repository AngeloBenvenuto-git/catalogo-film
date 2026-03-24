package com.progetto.catalogo_film.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GoogleMapsService {

    private final WebClient webClient;

    @Value("${google.maps.api.key}")
    private String apiKey;

    public GoogleMapsService() {
        this.webClient = WebClient.create();
    }

    public List<Map<String, Object>> trovaCinemaVicino(double lat, double lng) {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                + "?location=" + lat + "," + lng
                + "&radius=30000"
                + "&type=movie_theater"
                + "&key=" + apiKey
                + "&language=it";

        Map response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null) throw new RuntimeException("Nessun risultato da Google Maps");

        List<Map> results = (List<Map>) response.get("results");
        if (results == null || results.isEmpty())
            throw new RuntimeException("Nessun cinema trovato nella zona. Prova con una città più grande vicina.");

        return results.stream()
                .limit(5)
                .map(posto -> {
                    Map geometry = (Map) posto.get("geometry");
                    Map location = (Map) geometry.get("location");
                    return Map.<String, Object>of(
                            "nome", posto.getOrDefault("name", ""),
                            "indirizzo", posto.getOrDefault("vicinity", ""),
                            "lat", location.get("lat"),
                            "lng", location.get("lng")
                    );
                })
                .collect(Collectors.toList());
    }
}