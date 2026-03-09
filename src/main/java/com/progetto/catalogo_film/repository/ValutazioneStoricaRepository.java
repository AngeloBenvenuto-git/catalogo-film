package com.progetto.catalogo_film.repository;

import com.progetto.catalogo_film.entity.ValutazioneStorica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ValutazioneStoricaRepository extends JpaRepository<ValutazioneStorica, Long> {

    List<ValutazioneStorica> findByFilmIdOrderByDataDesc(Long filmId);

    List<ValutazioneStorica> findByRedattoreId(Long redattoreId);

    Optional<ValutazioneStorica> findTopByFilmIdOrderByDataDesc(Long filmId);

    long countByFilmId(Long filmId);

    long countByRedattoreId(Long redattoreId);
}