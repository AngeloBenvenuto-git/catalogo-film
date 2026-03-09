package com.progetto.catalogo_film.repository;

import com.progetto.catalogo_film.entity.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    Optional<Utente> findByEmail(String email);

    Optional<Utente> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<Utente> findByRuolo(Utente.Ruolo ruolo);

    List<Utente> findByBannato(Boolean bannato);

    List<Utente> findByRuoloAndBannato(Utente.Ruolo ruolo, Boolean bannato);

    List<Utente> findAllByOrderByDataRegistrazioneDesc();

    long countByRuolo(Utente.Ruolo ruolo);

    long countByBannato(Boolean bannato);
}