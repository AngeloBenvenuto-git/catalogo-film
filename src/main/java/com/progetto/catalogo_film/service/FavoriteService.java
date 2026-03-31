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
@Transactional // Indispensabile perché usiamo l'EntityManager nel DAO (specialmente per remove e save)
public class FavoriteService {

    private final FavoriteDAO favoriteDAO;
    private final UtenteDAO utenteDAO;
    private final FilmDAO filmDAO;

    // Uso il costruttore per l'iniezione: è la pratica migliore in Spring Boot 3
    public FavoriteService(FavoriteDAO favoriteDAO, UtenteDAO utenteDAO, FilmDAO filmDAO) {
        this.favoriteDAO = favoriteDAO;
        this.utenteDAO = utenteDAO;
        this.filmDAO = filmDAO;
    }

    /**
     * Recupera la lista dei preferiti per un determinato utente
     */
    @Transactional(readOnly = true)
    public List<Favorite> getFavorites(String username) {
        return favoriteDAO.findByUsername(username);
    }

    /**
     * Aggiunge un film ai preferiti se non è già presente
     */
    public Favorite addFavorite(String username, Long filmId) {
        // Controllo se esiste già usando il DAO manuale
        return favoriteDAO.findByUsernameAndFilmId(username, filmId)
                .orElseGet(() -> {
                    // Se non esiste, recupero Utente e Film dai rispettivi DAO
                    Utente utente = utenteDAO.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("Utente non trovato: " + username));

                    Film film = filmDAO.findById(filmId)
                            .orElseThrow(() -> new RuntimeException("Film non trovato con ID: " + filmId));

                    Favorite fav = new Favorite();
                    fav.setUtente(utente);
                    fav.setFilm(film);

                    // Salvo tramite l'EntityManager del DAO
                    return favoriteDAO.save(fav);
                });
    }

    /**
     * Rimuove un film dai preferiti
     */
    public void removeFavorite(String username, Long filmId) {
        // Il metodo deleteByUsernameAndFilmId del DAO userà executeUpdate()
        favoriteDAO.deleteByUsernameAndFilmId(username, filmId);
    }
}