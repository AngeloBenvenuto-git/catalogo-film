package com.progetto.catalogo_film.controller;

import com.progetto.catalogo_film.dto.MessaggioDTO;
import com.progetto.catalogo_film.entity.Messaggio;
import com.progetto.catalogo_film.entity.Utente;
import com.progetto.catalogo_film.service.MessaggioService;
import com.progetto.catalogo_film.service.UtenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messaggi")
public class MessaggioController {

    private final MessaggioService messaggioService;
    private final UtenteService utenteService;

    public MessaggioController(MessaggioService messaggioService, UtenteService utenteService) {
        this.messaggioService = messaggioService;
        this.utenteService = utenteService;
    }

    @GetMapping
    public ResponseEntity<List<MessaggioDTO>> getTuttiMessaggi() {
        return ResponseEntity.ok(
                messaggioService.getTuttiMessaggi()
                        .stream().map(MessaggioDTO::new).collect(Collectors.toList())
        );
    }

    @GetMapping("/miei")
    public ResponseEntity<List<MessaggioDTO>> getMieiMessaggi(
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {

        Utente utente = utenteService.getUtenteByEmail(userDetails.getUsername());

        return ResponseEntity.ok(
                messaggioService.getMessaggiPerUtente(utente.getUsername())
                        .stream().map(MessaggioDTO::new).collect(Collectors.toList())
        );
    }

    @GetMapping("/non-letti")
    public ResponseEntity<List<MessaggioDTO>> getMessaggiNonLetti() {
        return ResponseEntity.ok(
                messaggioService.getMessaggiNonLetti()
                        .stream().map(MessaggioDTO::new).collect(Collectors.toList())
        );
    }

    @GetMapping("/conta-non-letti")
    public ResponseEntity<Map<String, Long>> contaNonLetti() {
        return ResponseEntity.ok(Map.of("conteggio", messaggioService.contaNonLetti()));
    }

    @PostMapping
    public ResponseEntity<?> aggiungiMessaggio(
            @RequestBody Map<String, String> body,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        try {
            Utente mittente = utenteService.getUtenteByEmail(userDetails.getUsername());

            Messaggio messaggio = new Messaggio();
            messaggio.setMittente(mittente);
            messaggio.setOggetto(body.get("oggetto"));
            messaggio.setTesto(body.get("testo"));

            return ResponseEntity.ok(new MessaggioDTO(messaggioService.aggiungiMessaggio(messaggio)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }

    @PutMapping("/{id}/letto")
    public ResponseEntity<?> segnaComeLetto(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new MessaggioDTO(messaggioService.segnaComeLetto(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/rispondi")
    public ResponseEntity<?> rispondiAlMessaggio(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String risposta = body.get("risposta");
            return ResponseEntity.ok(new MessaggioDTO(messaggioService.rispondiAlMessaggio(id, risposta)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancellaMessaggio(@PathVariable Long id) {
        try {
            messaggioService.cancellaMessaggio(id);
            return ResponseEntity.ok(Map.of("messaggio", "Messaggio cancellato"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }
}