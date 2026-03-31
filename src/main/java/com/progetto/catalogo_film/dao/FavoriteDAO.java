package com.progetto.catalogo_film.dao;

import com.progetto.catalogo_film.entity.Favorite;
import java.util.List;
import java.util.Optional;

public interface FavoriteDAO {
    List<Favorite> findByUsername(String username);
    Optional<Favorite> findByUsernameAndFilmId(String username, Long filmId);
    void deleteByUsernameAndFilmId(String username, Long filmId);
    Favorite save(Favorite favorite);
}