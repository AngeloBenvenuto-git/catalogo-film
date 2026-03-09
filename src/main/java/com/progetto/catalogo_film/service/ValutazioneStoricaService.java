package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.entity.Film;
import com.progetto.catalogo_film.entity.Utente;
import com.progetto.catalogo_film.entity.ValutazioneStorica;
import com.progetto.catalogo_film.repository.FilmRepository;
import com.progetto.catalogo_film.repository.UtenteRepository;
import com.progetto.catalogo_film.repository.ValutazioneStoricaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValutazioneStoricaService {

    private final ValutazioneStoricaRepository valutazioneStoricaRepository;
    private final FilmRepository filmRepository;
    private final UtenteRepository utenteRepository;

    public ValutazioneStoricaService(ValutazioneStoricaRepository valutazioneStoricaRepository,
                                     FilmRepository filmRepository,
                                     UtenteRepository utenteRepository) {
        this.valutazioneStoricaRepository = valutazioneStoricaRepository;
        this.filmRepository = filmRepository;
        this.utenteRepository = utenteRepository;
    }

    public List<ValutazioneStorica> getStorico(Long filmId) {
        return valutazioneStoricaRepository.findByFilmIdOrderByDataDesc(filmId);
    }

    public ValutazioneStorica aggiornaValutazione(Long filmId, String email, Double nuovoVoto) {
        if (nuovoVoto < 0 || nuovoVoto > 10) {
            throw new RuntimeException("Il voto deve essere tra 0 e 10");
        }

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new RuntimeException("Film non trovato"));

        Utente redattore = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        ValutazioneStorica storico = new ValutazioneStorica();
        storico.setFilm(film);
        storico.setRedattore(redattore);
        storico.setVecchioVoto(film.getValutazione());
        storico.setNuovoVoto(nuovoVoto);

        film.setValutazione(nuovoVoto);
        filmRepository.save(film);

        return valutazioneStoricaRepository.save(storico);
    }
}