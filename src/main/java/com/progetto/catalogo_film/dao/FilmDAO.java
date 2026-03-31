package com.progetto.catalogo_film.dao;

import com.progetto.catalogo_film.entity.Film;
import java.util.List;
import java.util.Optional;

public interface FilmDAO {
    List<Film> findAll();
    Optional<Film> findById(Long id);
    Film save(Film film);
    void deleteById(Long id);

    // Metodi aggiuntivi necessari per il FilmService
    List<Film> findByTitoloContainingIgnoreCase(String titolo);
    List<Film> findByTipologia(String tipologia);
    List<Film> findByAnno(Integer anno);
    List<Film> findAllByOrderByValutazioneDesc();
    List<Film> findAllByOrderByAnnoDesc();
}