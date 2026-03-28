package com.progetto.catalogo_film.controller;

import com.progetto.catalogo_film.entity.Utente;
import com.progetto.catalogo_film.service.UtenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UtenteService utenteService;

    public AuthController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String token = utenteService.login(
                    body.get("email"),
                    body.get("password")
            );
            return ResponseEntity.ok(Map.of("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }

    @PostMapping("/registra")
    public ResponseEntity<?> registra(@RequestBody Map<String, String> body) {
        try {
            Utente utente = utenteService.registra(
                    body.get("username"),
                    body.get("email"),
                    body.get("password")
            );
            return ResponseEntity.ok(Map.of(
                    "messaggio", "Registrazione completata",
                    "username", utente.getUsername()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }

    @PutMapping("/user/update")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, String> body,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        try {
            utenteService.aggiornaProfilo(
                    userDetails.getUsername(),
                    body.get("username"),
                    body.get("password"),
                    body.get("fotoBase64")
            );
            return ResponseEntity.ok(Map.of("messaggio", "Profilo aggiornato con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        try {
            Utente utente = utenteService.getUtenteByEmail(userDetails.getUsername());
            return ResponseEntity.ok(new com.progetto.catalogo_film.dto.UtenteDTO(utente));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }
}