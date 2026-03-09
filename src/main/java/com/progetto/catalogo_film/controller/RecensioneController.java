package com.progetto.catalogo_film.controller;

import com.progetto.catalogo_film.dto.RecensioneDTO;
import com.progetto.catalogo_film.entity.Recensione;
import com.progetto.catalogo_film.service.RecensioneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recensioni")
public class RecensioneController {

    private final RecensioneService recensioneService;

    public RecensioneController(RecensioneService recensioneService) {
        this.recensioneService = recensioneService;
    }

    @GetMapping("/film/{filmId}")
    public ResponseEntity<List<RecensioneDTO>> getRecensioniFilm(@PathVariable Long filmId) {
        return ResponseEntity.ok(
                recensioneService.getRecensioniFilm(filmId)
                        .stream().map(RecensioneDTO::new).collect(Collectors.toList())
        );
    }

    @GetMapping("/utente/{utenteId}")
    public ResponseEntity<List<RecensioneDTO>> getRecensioniUtente(@PathVariable Long utenteId) {
        return ResponseEntity.ok(
                recensioneService.getRecensioniUtente(utenteId)
                        .stream().map(RecensioneDTO::new).collect(Collectors.toList())
        );
    }

    @PostMapping("/film/{filmId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> aggiungiRecensione(
            @PathVariable Long filmId,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Recensione recensione = recensioneService.aggiungiRecensione(
                    filmId,
                    userDetails.getUsername(),
                    (String) body.get("testo"),
                    (Integer) body.get("voto")
            );
            return ResponseEntity.ok(new RecensioneDTO(recensione));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancellaRecensione(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            recensioneService.cancellaRecensione(id, userDetails.getUsername());
            return ResponseEntity.ok(Map.of("messaggio", "Recensione cancellata"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }
}