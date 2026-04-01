package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.dao.FavoriteDAO;
import com.progetto.catalogo_film.dao.FilmDAO;
import com.progetto.catalogo_film.dao.UtenteDAO;
import com.progetto.catalogo_film.entity.Favorite;
import com.progetto.catalogo_film.entity.Film;
import com.progetto.catalogo_film.entity.Utente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FavoriteService {

    private final FavoriteDAO favoriteDAO;
    private final UtenteDAO utenteDAO;
    private final FilmDAO filmDAO;

    public FavoriteService(FavoriteDAO favoriteDAO, UtenteDAO utenteDAO, FilmDAO filmDAO) {
        this.favoriteDAO = favoriteDAO;
        this.utenteDAO = utenteDAO;
        this.filmDAO = filmDAO;
    }

    @Transactional(readOnly = true)
    public List<Favorite> getFavorites(String username) {
        return favoriteDAO.findByUsername(username);
    }

    public Favorite addFavorite(String username, Long filmId) {
        return favoriteDAO.findByUsernameAndFilmId(username, filmId)
                .orElseGet(() -> {
                    Utente utente = utenteDAO.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("Utente non trovato: " + username));

                    Film film = filmDAO.findById(filmId)
                            .orElseThrow(() -> new RuntimeException("Film non trovato con ID: " + filmId));

                    Favorite fav = new Favorite();
                    fav.setUtente(utente);
                    fav.setFilm(film);
                    return favoriteDAO.save(fav);
                });
    }

    public void removeFavorite(String username, Long filmId) {
        favoriteDAO.deleteByUsernameAndFilmId(username, filmId);
    }
}