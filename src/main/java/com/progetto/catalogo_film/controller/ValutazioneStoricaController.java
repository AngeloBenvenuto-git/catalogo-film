package com.progetto.catalogo_film.controller;

import com.progetto.catalogo_film.dto.ValutazioneStoricaDTO;
import com.progetto.catalogo_film.service.ValutazioneStoricaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/valutazioni")
public class ValutazioneStoricaController {

    private final ValutazioneStoricaService valutazioneStoricaService;

    public ValutazioneStoricaController(ValutazioneStoricaService valutazioneStoricaService) {
        this.valutazioneStoricaService = valutazioneStoricaService;
    }

    @GetMapping("/film/{filmId}")
    public ResponseEntity<List<ValutazioneStoricaDTO>> getStorico(@PathVariable Long filmId) {
        return ResponseEntity.ok(
                valutazioneStoricaService.getStorico(filmId)
                        .stream().map(ValutazioneStoricaDTO::new).collect(Collectors.toList())
        );
    }

    @PutMapping("/film/{filmId}")
    @PreAuthorize("hasAnyRole('REDATTORE', 'ADMIN')")
    public ResponseEntity<?> aggiornaValutazione(
            @PathVariable Long filmId,
            @RequestBody Map<String, Double> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(new ValutazioneStoricaDTO(
                    valutazioneStoricaService.aggiornaValutazione(
                            filmId,
                            userDetails.getUsername(),
                            body.get("nuovoVoto")
                    )
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }
}