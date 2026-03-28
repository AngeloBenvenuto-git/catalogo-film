package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.entity.Favorite;
import com.progetto.catalogo_film.entity.Film;
import com.progetto.catalogo_film.entity.Utente;
import com.progetto.catalogo_film.repository.FavoriteRepository;
import com.progetto.catalogo_film.repository.FilmRepository;
import com.progetto.catalogo_film.repository.UtenteRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private FilmRepository filmRepository;

    public List<Favorite> getFavorites(String username) {
        return favoriteRepository.findByUtente_Username(username);
    }

    public Object addFavorite(String username, Long filmId) {
        System.out.println("Service: Tento di aggiungere film " + filmId + " per utente " + username);

        return favoriteRepository.findByUtente_UsernameAndFilm_Id(username, filmId)
                .map(existing -> {
                    System.out.println("Service: Film già presente, restituisco l'esistente.");
                    return (Object) existing;
                })
                .orElseGet(() -> {
                    System.out.println("Service: Nuovo preferito, procedo al salvataggio.");

                    // 1. Peschiamo i veri oggetti dal DB
                    Utente utente = utenteRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("Utente non trovato"));

                    Film film = filmRepository.findById(filmId)
                            .orElseThrow(() -> new RuntimeException("Film non trovato"));

                    // 2. Li leghiamo insieme
                    Favorite fav = new Favorite();
                    fav.setUtente(utente);
                    fav.setFilm(film);

                    return favoriteRepository.save(fav);
                });
    }

    public void removeFavorite(String username, Long filmId) {
        System.out.println("Service: Rimuovo film " + filmId + " per utente " + username);
        favoriteRepository.deleteByUtente_UsernameAndFilm_Id(username, filmId);
    }
}