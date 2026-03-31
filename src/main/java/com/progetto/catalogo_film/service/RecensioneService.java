package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.dao.FilmDAO;
import com.progetto.catalogo_film.dao.RecensioneDAO;
import com.progetto.catalogo_film.dao.UtenteDAO;
import com.progetto.catalogo_film.entity.Film;
import com.progetto.catalogo_film.entity.Recensione;
import com.progetto.catalogo_film.entity.Utente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RecensioneService {

    private final RecensioneDAO recensioneDAO;
    private final FilmDAO filmDAO;
    private final UtenteDAO utenteDAO;

    public RecensioneService(RecensioneDAO recensioneDAO,
                             FilmDAO filmDAO,
                             UtenteDAO utenteDAO) {
        this.recensioneDAO = recensioneDAO;
        this.filmDAO = filmDAO;
        this.utenteDAO = utenteDAO;
    }

    @Transactional(readOnly = true)
    public List<Recensione> getRecensioniUtente(Long utenteId) {
        // Usiamo il DAO per filtrare le recensioni dell'utente
        // Nota: recuperiamo tutte le recensioni dal DAO e filtriamo per ID utente
        return recensioneDAO.findByFilmId(null).stream()
                .filter(r -> r.getUtente().getId().equals(utenteId))
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Recensione> getRecensioniFilm(Long filmId) {
        // Il DAO deve implementare findByFilmId (ordinato per data nel DAO se necessario)
        return recensioneDAO.findByFilmId(filmId);
    }

    public Recensione aggiungiRecensione(Long filmId, String email, String testo, Integer voto) {
        if (voto < 1 || voto > 10) {
            throw new RuntimeException("Il voto deve essere tra 1 e 10");
        }

        Film film = filmDAO.findById(filmId)
                .orElseThrow(() -> new RuntimeException("Film non trovato"));

        Utente utente = utenteDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Controllo se l'utente ha già recensito il film usando il DAO
        boolean giaRecensito = recensioneDAO.findByFilmId(filmId).stream()
                .anyMatch(r -> r.getUtente().getId().equals(utente.getId()));

        if (giaRecensito) {
            throw new RuntimeException("Hai già recensito questo film");
        }

        Recensione recensione = new Recensione();
        recensione.setFilm(film);
        recensione.setUtente(utente);
        recensione.setTesto(testo);
        recensione.setVoto(voto);

        Recensione salvata = recensioneDAO.save(recensione);
        aggiornaValutazioneMedia(film);

        return salvata;
    }

    public void cancellaRecensione(Long id, String email) {
        // Recupero la recensione (il DAO deve avere findById o findByFilmId filtrato)
        // Se non hai findById nel RecensioneDAO, usa l'EntityManager nel DAOImpl
        Recensione recensione = recensioneDAO.findByFilmId(null).stream() // Esempio logico, meglio avere findById
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Recensione non trovata"));

        Utente utente = utenteDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        boolean isAdmin = utente.getRuolo().name().contains("ADMIN");
        boolean isProprietario = recensione.getUtente().getId().equals(utente.getId());

        if (!isAdmin && !isProprietario) {
            throw new RuntimeException("Non puoi cancellare questa recensione");
        }

        Film film = recensione.getFilm();
        recensioneDAO.deleteById(id);
        aggiornaValutazioneMedia(film);
    }

    private void aggiornaValutazioneMedia(Film film) {
        List<Recensione> recensioni = recensioneDAO.findByFilmId(film.getId());
        if (recensioni.isEmpty()) {
            film.setValutazione(0.0);
        } else {
            double media = recensioni.stream()
                    .mapToInt(Recensione::getVoto)
                    .average()
                    .orElse(0.0);
            // Arrotondamento a una cifra decimale
            film.setValutazione(Math.round(media * 10.0) / 10.0);
        }
        // Salviamo il film con la nuova media tramite il FilmDAO
        filmDAO.save(film);
    }
}