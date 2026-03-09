package com.progetto.catalogo_film.repository;

import com.progetto.catalogo_film.entity.Recensione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

    List<Recensione> findByFilmId(Long filmId);

    List<Recensione> findByUtenteId(Long utenteId);

    List<Recensione> findByFilmIdOrderByDataRecensioneDesc(Long filmId);

    List<Recensione> findByFilmIdOrderByVotoDesc(Long filmId);

    Optional<Recensione> findByFilmIdAndUtenteId(Long filmId, Long utenteId);

    boolean existsByFilmIdAndUtenteId(Long filmId, Long utenteId);

    long countByFilmId(Long filmId);

    long countByUtenteId(Long utenteId);
}