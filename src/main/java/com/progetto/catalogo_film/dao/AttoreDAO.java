package com.progetto.catalogo_film.dao;
import com.progetto.catalogo_film.entity.Attore;
import java.util.List;
import java.util.Optional;

public interface AttoreDAO {
    List<Attore> findAll();
    Optional<Attore> findById(Long id);
    Attore save(Attore attore);
    void deleteById(Long id);
}