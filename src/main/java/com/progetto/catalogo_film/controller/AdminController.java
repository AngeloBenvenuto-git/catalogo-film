package com.progetto.catalogo_film.controller;

import com.progetto.catalogo_film.dto.UtenteDTO;
import com.progetto.catalogo_film.service.UtenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UtenteService utenteService;

    public AdminController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @GetMapping("/utenti")
    public ResponseEntity<List<UtenteDTO>> getTuttiUtenti() {
        List<UtenteDTO> utenti = utenteService.getTuttiUtenti()
                .stream()
                .map(UtenteDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(utenti);
    }

    @GetMapping("/utenti/{id}")
    public ResponseEntity<UtenteDTO> getUtente(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new UtenteDTO(utenteService.getUtente(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/utenti/{id}/banna")
    public ResponseEntity<?> bannaUtente(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new UtenteDTO(utenteService.bannaUtente(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }

    @PutMapping("/utenti/{id}/sbanna")
    public ResponseEntity<?> sbannaUtente(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new UtenteDTO(utenteService.sbannaUtente(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }

    @PutMapping("/utenti/{id}/promuovi")
    public ResponseEntity<?> promuoviARedattore(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new UtenteDTO(utenteService.promuoviARedattore(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }

    @DeleteMapping("/utenti/{id}")
    public ResponseEntity<?> eliminaUtente(@PathVariable Long id) {
        try {
            utenteService.eliminaUtente(id);
            return ResponseEntity.ok(Map.of("messaggio", "Utente eliminato con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }
}