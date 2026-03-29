package com.progetto.catalogo_film.controller;

import com.progetto.catalogo_film.dto.FavoriteRequest;
import com.progetto.catalogo_film.dto.FilmDTO;
import com.progetto.catalogo_film.entity.Favorite;
import com.progetto.catalogo_film.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;
    @GetMapping("/{username}")
    public ResponseEntity<?> getFavorites(@PathVariable String username) {
        List<Map<String, Object>> favorites = favoriteService.getFavorites(username)
                .stream().map(fav -> Map.of(
                        "id", fav.getId(),
                        "film", new FilmDTO(fav.getFilm())
                )).collect(Collectors.toList());

        return ResponseEntity.ok(favorites);
    }

    @PostMapping
    public ResponseEntity<?> addFavorite(@RequestBody FavoriteRequest request) {
        try {
            Favorite fav = (Favorite) favoriteService.addFavorite(request.getUsername(), request.getFilmId());
            return ResponseEntity.ok(Map.of("messaggio", "Aggiunto con successo", "id", fav.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }

    @DeleteMapping("/{username}/{filmId}")
    public ResponseEntity<?> removeFavorite(@PathVariable String username, @PathVariable Long filmId) {
        try {
            favoriteService.removeFavorite(username, filmId);
            return ResponseEntity.ok(Map.of("messaggio", "Rimosso con successo"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }
}