package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.entity.Film;
import com.progetto.catalogo_film.entity.Recensione;
import com.progetto.catalogo_film.entity.Utente;
import com.progetto.catalogo_film.repository.FilmRepository;
import com.progetto.catalogo_film.repository.RecensioneRepository;
import com.progetto.catalogo_film.repository.UtenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecensioneService {

    private final RecensioneRepository recensioneRepository;
    private final FilmRepository filmRepository;
    private final UtenteRepository utenteRepository;

    public RecensioneService(RecensioneRepository recensioneRepository,
                             FilmRepository filmRepository,
                             UtenteRepository utenteRepository) {
        this.recensioneRepository = recensioneRepository;
        this.filmRepository = filmRepository;
        this.utenteRepository = utenteRepository;
    }

    public List<Recensione> getRecensioniFilm(Long filmId) {
        return recensioneRepository.findByFilmIdOrderByDataRecensioneDesc(filmId);
    }

    public List<Recensione> getRecensioniUtente(Long utenteId) {
        return recensioneRepository.findByUtenteId(utenteId);
    }

    public Recensione aggiungiRecensione(Long filmId, String email, String testo, Integer voto) {
        if (voto < 1 || voto > 10) {
            throw new RuntimeException("Il voto deve essere tra 1 e 10");
        }

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new RuntimeException("Film non trovato"));

        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (recensioneRepository.existsByFilmIdAndUtenteId(filmId, utente.getId())) {
            throw new RuntimeException("Hai già recensito questo film");
        }

        Recensione recensione = new Recensione();
        recensione.setFilm(film);
        recensione.setUtente(utente);
        recensione.setTesto(testo);
        recensione.setVoto(voto);

        Recensione salvata = recensioneRepository.save(recensione);
        aggiornaValutazioneMedia(film);
        return salvata;
    }

    public void cancellaRecensione(Long id, String email) {
        Recensione recensione = recensioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recensione non trovata"));

        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        boolean isAdmin = utente.getRuolo() == Utente.Ruolo.ADMIN;
        boolean isProprietario = recensione.getUtente().getId().equals(utente.getId());

        if (!isAdmin && !isProprietario) {
            throw new RuntimeException("Non puoi cancellare questa recensione");
        }

        Film film = recensione.getFilm();
        recensioneRepository.deleteById(id);
        aggiornaValutazioneMedia(film);
    }

    private void aggiornaValutazioneMedia(Film film) {
        List<Recensione> recensioni = recensioneRepository.findByFilmId(film.getId());
        if (recensioni.isEmpty()) {
            film.setValutazione(0.0);
        } else {
            double media = recensioni.stream()
                    .mapToInt(Recensione::getVoto)
                    .average()
                    .orElse(0.0);
            film.setValutazione(Math.round(media * 10.0) / 10.0);
        }
        filmRepository.save(film);
    }
}
