package com.progetto.catalogo_film.dao;
import com.progetto.catalogo_film.entity.Recensione;
import java.util.List;

public interface RecensioneDAO {
    List<Recensione> findByFilmId(Long filmId);
    Recensione save(Recensione recensione);
    void deleteById(Long id);
}