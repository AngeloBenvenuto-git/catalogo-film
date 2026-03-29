package com.progetto.catalogo_film.repository;

import com.progetto.catalogo_film.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUtente_Username(String username);

    Optional<Favorite> findByUtente_UsernameAndFilm_Id(String username, Long filmId);

    @Modifying
    void deleteByUtente_UsernameAndFilm_Id(String username, Long filmId);
}