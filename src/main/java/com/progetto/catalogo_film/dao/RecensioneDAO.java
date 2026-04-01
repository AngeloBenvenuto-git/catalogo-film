package com.progetto.catalogo_film.dao;

import com.progetto.catalogo_film.entity.Recensione;
import java.util.List;
import java.util.Optional;

public interface RecensioneDAO {
    List<Recensione> findByFilmId(Long filmId);

    Optional<Recensione> findById(Long id);
    List<Recensione> findByUtenteId(Long utenteId);

    Recensione save(Recensione r);
    void deleteById(Long id);
}