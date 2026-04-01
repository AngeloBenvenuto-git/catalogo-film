package com.progetto.catalogo_film.dao.impl;

import com.progetto.catalogo_film.dao.UtenteDAO;
import com.progetto.catalogo_film.entity.Utente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UtenteDAOImpl implements UtenteDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Utente> findByUsername(String username) {
        try {
            Utente utente = entityManager.createQuery(
                            "SELECT u FROM Utente u WHERE u.username = :username", Utente.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(utente);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Utente> findByEmail(String email) {
        try {
            Utente utente = entityManager.createQuery(
                            "SELECT u FROM Utente u WHERE u.email = :email", Utente.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(utente);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Utente save(Utente utente) {
        if (utente.getId() == null) {
            entityManager.persist(utente);
            return utente;
        } else {
            return entityManager.merge(utente);
        }
    }

    @Override
    public List<Utente> findAll() {
        return entityManager.createQuery("SELECT u FROM Utente u", Utente.class)
                .getResultList();
    }

    @Override
    public Optional<Utente> findById(Long id) {
        Utente utente = entityManager.find(Utente.class, id);
        return Optional.ofNullable(utente);
    }
}