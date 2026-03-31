package com.progetto.catalogo_film.dao;

import com.progetto.catalogo_film.entity.Utente;
import java.util.List;
import java.util.Optional;

public interface UtenteDAO {
    Optional<Utente> findByUsername(String username);
    Optional<Utente> findByEmail(String email);
    Utente save(Utente utente);
    List<Utente> findAll();
    Optional<Utente> findById(Long id);
}