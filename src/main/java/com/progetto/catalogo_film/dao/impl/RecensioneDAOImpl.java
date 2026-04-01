package com.progetto.catalogo_film.dao.impl;

import com.progetto.catalogo_film.dao.RecensioneDAO;
import com.progetto.catalogo_film.entity.Recensione;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class RecensioneDAOImpl implements RecensioneDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Recensione> findByFilmId(Long filmId) {
        return entityManager.createQuery("SELECT r FROM Recensione r WHERE r.film.id = :id", Recensione.class)
                .setParameter("id", filmId)
                .getResultList();
    }

    @Override
    public Recensione save(Recensione r) {
        if (r.getId() == null) {
            entityManager.persist(r);
            return r;
        } else {
            return entityManager.merge(r);
        }
    }

    @Override
    public void deleteById(Long id) {
        Recensione r = entityManager.find(Recensione.class, id);
        if (r != null) {
            entityManager.remove(r);
        }
    }

    @Override
    public Optional<Recensione> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Recensione.class, id));
    }

    @Override
    public List<Recensione> findByUtenteId(Long utenteId) {
        return entityManager.createQuery("SELECT r FROM Recensione r WHERE r.utente.id = :id", Recensione.class)
                .setParameter("id", utenteId)
                .getResultList();
    }
}