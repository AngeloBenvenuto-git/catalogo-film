package com.progetto.catalogo_film.controller;

import com.progetto.catalogo_film.dto.FavoriteRequest;
import com.progetto.catalogo_film.entity.Favorite;
import com.progetto.catalogo_film.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "http://localhost:4200")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/{username}")
    public List<Favorite> getFavorites(@PathVariable String username) {
        return favoriteService.getFavorites(username);
    }

    @PostMapping
    public Object addFavorite(@RequestBody FavoriteRequest request) {
        System.out.println("ARRIVATO: username=" + request.getUsername() + " filmId=" + request.getFilmId());
        return favoriteService.addFavorite(request.getUsername(), request.getFilmId());
    }

    @DeleteMapping("/{username}/{filmId}")
    public void removeFavorite(@PathVariable String username, @PathVariable Long filmId) {
        favoriteService.removeFavorite(username, filmId);
    }
}


