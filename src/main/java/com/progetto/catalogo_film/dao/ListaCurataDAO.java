package com.progetto.catalogo_film.dao;

import com.progetto.catalogo_film.entity.ListaCurata;
import java.util.List;
import java.util.Optional;

public interface ListaCurataDAO {
    List<ListaCurata> findAll();
    List<ListaCurata> findAllSorted();
    List<ListaCurata> findByRedattoreId(Long redattoreId);
    Optional<ListaCurata> findById(Long id);
    ListaCurata save(ListaCurata listaCurata);
    void deleteById(Long id);
    boolean existsByTitoloAndRedattoreId(String titolo, Long redattoreId);
}