package com.progetto.catalogo_film.repository;

import com.progetto.catalogo_film.entity.Genere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GenereRepository extends JpaRepository<Genere, Long> {

    Optional<Genere> findByNome(String nome);

    boolean existsByNome(String nome);

    List<Genere> findAllByOrderByNomeAsc();

    List<Genere> findByNomeContainingIgnoreCase(String nome);

    Optional<Genere> findByTmdbId(Integer tmdbId);

    boolean existsByTmdbId(Integer tmdbId);
}