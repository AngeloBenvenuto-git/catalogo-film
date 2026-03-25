package com.progetto.catalogo_film.repository;

import com.progetto.catalogo_film.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUsername(String username);

    // Serve per il controllo nel Service
    Optional<Favorite> findByUsernameAndFilmId(String username, Long filmId);

    boolean existsByUsernameAndFilmId(String username, Long filmId);

    @Modifying // OBBLIGATORIO per le delete custom
    void deleteByUsernameAndFilmId(String username, Long filmId);
}