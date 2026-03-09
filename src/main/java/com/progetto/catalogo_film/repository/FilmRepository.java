package com.progetto.catalogo_film.repository;

import com.progetto.catalogo_film.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {

    List<Film> findByTitoloContainingIgnoreCase(String titolo);

    List<Film> findByTipologia(String tipologia);

    List<Film> findByAnno(Integer anno);

    List<Film> findByAnnoBetween(Integer annoInizio, Integer annoFine);

    Optional<Film> findByTmdbId(Integer tmdbId);

    List<Film> findAllByOrderByValutazioneDesc();

    List<Film> findAllByOrderByAnnoDesc();

    List<Film> findAllByOrderByTitoloAsc();

    List<Film> findByTipologiaOrderByValutazioneDesc(String tipologia);

    List<Film> findByTipologiaOrderByAnnoDesc(String tipologia);

    List<Film> findByValutazioneGreaterThanEqual(Double valutazione);

    boolean existsByTmdbId(Integer tmdbId);

    long countByTipologia(String tipologia);
}