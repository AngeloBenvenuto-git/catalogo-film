package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.dao.FilmDAO;
import com.progetto.catalogo_film.dao.UtenteDAO;
import com.progetto.catalogo_film.dao.ValutazioneStoricaDAO;
import com.progetto.catalogo_film.entity.Film;
import com.progetto.catalogo_film.entity.Utente;
import com.progetto.catalogo_film.entity.ValutazioneStorica;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional // Indispensabile per garantire l'atomicità delle due operazioni di save
public class ValutazioneStoricaService {

    private final ValutazioneStoricaDAO valutazioneStoricaDAO;
    private final FilmDAO filmDAO;
    private final UtenteDAO utenteDAO;

    public ValutazioneStoricaService(ValutazioneStoricaDAO valutazioneStoricaDAO,
                                     FilmDAO filmDAO,
                                     UtenteDAO utenteDAO) {
        this.valutazioneStoricaDAO = valutazioneStoricaDAO;
        this.filmDAO = filmDAO;
        this.utenteDAO = utenteDAO;
    }

    @Transactional(readOnly = true)
    public List<ValutazioneStorica> getStorico(Long filmId) {
        // Recupera la cronologia delle valutazioni per un film tramite il DAO manuale
        return valutazioneStoricaDAO.findAll().stream()
                .filter(v -> v.getFilm().getId().equals(filmId))
                .toList();
    }

    public ValutazioneStorica aggiornaValutazione(Long filmId, String email, Double nuovoVoto) {
        // Validazione del range del voto
        if (nuovoVoto < 0 || nuovoVoto > 10) {
            throw new RuntimeException("Il voto critico deve essere compreso tra 0 e 10");
        }

        // Recupero Film tramite FilmDAO
        Film film = filmDAO.findById(filmId)
                .orElseThrow(() -> new RuntimeException("Film non trovato con ID: " + filmId));

        // Recupero Redattore tramite UtenteDAO
        Utente redattore = utenteDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato nel sistema"));

        // Creazione dell'entità storica per tracciare il cambiamento
        ValutazioneStorica storico = new ValutazioneStorica();
        storico.setFilm(film);
        storico.setRedattore(redattore);
        storico.setVecchioVoto(film.getValutazione());
        storico.setNuovoVoto(nuovoVoto);

        // 1. Aggiorniamo la valutazione corrente sul film (Stato Managed)
        film.setValutazione(nuovoVoto);
        filmDAO.save(film);

        // 2. Salviamo il record storico tramite ValutazioneStoricaDAO
        return valutazioneStoricaDAO.save(storico);
    }
}