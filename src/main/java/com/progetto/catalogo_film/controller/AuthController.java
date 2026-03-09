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
}