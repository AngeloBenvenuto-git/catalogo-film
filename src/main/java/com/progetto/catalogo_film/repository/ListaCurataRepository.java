package com.progetto.catalogo_film.repository;

import com.progetto.catalogo_film.entity.ListaCurata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ListaCurataRepository extends JpaRepository<ListaCurata, Long> {

    List<ListaCurata> findByRedattoreId(Long redattoreId);

    List<ListaCurata> findByTitoloContainingIgnoreCase(String titolo);

    List<ListaCurata> findAllByOrderByDataCreazioneDesc();

    List<ListaCurata> findByRedattoreIdOrderByDataCreazioneDesc(Long redattoreId);

    boolean existsByTitoloAndRedattoreId(String titolo, Long redattoreId);

    long countByRedattoreId(Long redattoreId);
}