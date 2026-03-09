package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.entity.Film;
import com.progetto.catalogo_film.repository.FilmRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmService {

    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    public List<Film> getTuttiFilm() {
        return filmRepository.findAll();
    }

    public Film getFilmById(Long id) {
        return filmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Film non trovato"));
    }

    public List<Film> cercaPerTitolo(String titolo) {
        return filmRepository.findByTitoloContainingIgnoreCase(titolo);
    }

    public List<Film> cercaPerTipologia(String tipologia) {
        return filmRepository.findByTipologia(tipologia);
    }

    public List<Film> cercaPerAnno(Integer anno) {
        return filmRepository.findByAnno(anno);
    }

    public List<Film> ordinaPerValutazione() {
        return filmRepository.findAllByOrderByValutazioneDesc();
    }

    public List<Film> ordinaPerAnno() {
        return filmRepository.findAllByOrderByAnnoDesc();
    }

    public List<Film> cercaPerGenere(Long genereId) {
        return filmRepository.findAll().stream()
                .filter(f -> f.getGeneri().stream()
                        .anyMatch(g -> g.getId().equals(genereId)))
                .toList();
    }

    public Film aggiungiFilm(Film film) {
        return filmRepository.save(film);
    }

    public Film modificaFilm(Long id, Film filmModificato) {
        Film film = getFilmById(id);
        film.setTitolo(filmModificato.getTitolo());
        film.setTrama(filmModificato.getTrama());
        film.setAnno(filmModificato.getAnno());
        film.setDurata(filmModificato.getDurata());
        film.setTipologia(filmModificato.getTipologia());
        film.setValutazione(filmModificato.getValutazione());
        return filmRepository.save(film);
    }

    public void cancellaFilm(Long id) {
        filmRepository.deleteById(id);
    }
}