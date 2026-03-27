package com.progetto.catalogo_film.controller;

import com.progetto.catalogo_film.dto.ListaCurataDTO;
import com.progetto.catalogo_film.service.ListaCurataService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.progetto.catalogo_film.entity.ListaCurata;
import com.progetto.catalogo_film.entity.Utente;
import com.progetto.catalogo_film.repository.ListaCurataRepository;
import com.progetto.catalogo_film.repository.UtenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/liste")
public class ListaCurataController {

    private final ListaCurataService listaCurataService;
    private final ListaCurataRepository listaCurataRepository;
    private final UtenteRepository utenteRepository;

    public ListaCurataController(ListaCurataService listaCurataService,ListaCurataRepository listaCurataRepository, UtenteRepository utenteRepository) {
        this.listaCurataService = listaCurataService;
        this.listaCurataRepository = listaCurataRepository;
        this.utenteRepository = utenteRepository;
    }

    @GetMapping
    public ResponseEntity<List<ListaCurataDTO>> getTutteListe() {
        return ResponseEntity.ok(
                listaCurataService.getTutteListe()
                        .stream().map(ListaCurataDTO::new).collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListaCurataDTO> getListaById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new ListaCurataDTO(listaCurataService.getListaById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/mie")
    @PreAuthorize("hasAnyRole('REDATTORE', 'ADMIN')")
    public ResponseEntity<List<ListaCurataDTO>> getListeRedattore(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                listaCurataService.getListeRedattore(userDetails.getUsername())
                        .stream().map(ListaCurataDTO::new).collect(Collectors.toList())
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('REDATTORE', 'ADMIN')")
    public ResponseEntity<?> creaLista(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(new ListaCurataDTO(listaCurataService.creaLista(
                    userDetails.getUsername(),
                    body.get("titolo"),
                    body.get("descrizione")
            )));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }

    @PostMapping("/{listaId}/film/{filmId}")
    @PreAuthorize("hasAnyRole('REDATTORE', 'ADMIN')")
    public ResponseEntity<?> aggiungiFilm(
            @PathVariable Long listaId,
            @PathVariable Long filmId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(new ListaCurataDTO(listaCurataService.aggiungiFilm(
                    listaId, filmId, userDetails.getUsername())));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }

    @DeleteMapping("/{listaId}/film/{filmId}")
    @PreAuthorize("hasAnyRole('REDATTORE', 'ADMIN')")
    public ResponseEntity<?> rimuoviFilm(
            @PathVariable Long listaId,
            @PathVariable Long filmId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(new ListaCurataDTO(listaCurataService.rimuoviFilm(
                    listaId, filmId, userDetails.getUsername())));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('REDATTORE', 'ADMIN')")
    public ResponseEntity<?> cancellaLista(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            listaCurataService.cancellaLista(id, userDetails.getUsername());
            return ResponseEntity.ok(Map.of("messaggio", "Lista cancellata"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('REDATTORE', 'ADMIN')")
    public ResponseEntity<?> aggiornaDatiLista(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            listaCurataService.aggiornaDatiLista(
                    id,
                    body.get("titolo"),
                    body.get("descrizione"),
                    userDetails.getUsername()
            );
            return ResponseEntity.ok(Map.of("messaggio", "Lista aggiornata con successo"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }
    @PostMapping("/{id}/like")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(new ListaCurataDTO(
                    listaCurataService.toggleLike(id, userDetails.getUsername())));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }
    @GetMapping("/liked")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ListaCurataDTO>> getListeLiked(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                listaCurataService.getListeLikedDaUtente(userDetails.getUsername())
                        .stream().map(ListaCurataDTO::new).collect(Collectors.toList())
        );
    }
}