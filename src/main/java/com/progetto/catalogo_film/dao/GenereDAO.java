package com.progetto.catalogo_film.dao;
import com.progetto.catalogo_film.entity.Genere;
import java.util.List;
import java.util.Optional;

public interface GenereDAO {
    List<Genere> findAll();
    Optional<Genere> findByNome(String nome);
}