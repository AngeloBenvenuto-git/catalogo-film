package com.progetto.catalogo_film.dao.impl;

import com.progetto.catalogo_film.dao.AttoreDAO;
import com.progetto.catalogo_film.entity.Attore;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class AttoreDAOImpl implements AttoreDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Attore> findAll() {
        return entityManager.createQuery("SELECT a FROM Attore a", Attore.class).getResultList();
    }

    @Override
    public Optional<Attore> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Attore.class, id));
    }

    @Override
    public Attore save(Attore a) {
        if (a.getId() == null) {
            entityManager.persist(a);
            return a;
        } else {
            return entityManager.merge(a);
        }
    }

    @Override
    public void deleteById(Long id) {
        Attore a = entityManager.find(Attore.class, id);
        if (a != null) {
            entityManager.remove(a);
        }
    }
}