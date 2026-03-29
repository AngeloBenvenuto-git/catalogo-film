package com.progetto.catalogo_film.repository;

import com.progetto.catalogo_film.entity.Attore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttoreRepository extends JpaRepository<Attore, Long> {

    Optional<Attore> findByTmdbId(Integer tmdbId);

    boolean existsByTmdbId(Integer tmdbId);

    List<Attore> findByNomeContainingIgnoreCase(String nome);

    List<Attore> findAllByOrderByNomeAsc();
}