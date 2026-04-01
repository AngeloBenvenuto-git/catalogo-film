package com.progetto.catalogo_film.dao;

import com.progetto.catalogo_film.entity.Messaggio;
import java.util.List;
import java.util.Optional;

public interface MessaggioDAO {
    List<Messaggio> findAllSorted();
    List<Messaggio> findByLetto(Boolean letto);
    List<Messaggio> findByUsername(String username);
    List<Messaggio> searchByOggetto(String oggetto);
    long countUnread(Boolean letto);
    Messaggio save(Messaggio messaggio);
    Optional<Messaggio> findById(Long id);
    void deleteById(Long id);
}