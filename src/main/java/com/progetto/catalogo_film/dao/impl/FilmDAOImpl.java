package com.progetto.catalogo_film.dao.impl;

import com.progetto.catalogo_film.dao.FilmDAO;
import com.progetto.catalogo_film.entity.Film;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmDAOImpl implements FilmDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Film> findAll() {
        return entityManager.createQuery("SELECT f FROM Film f", Film.class).getResultList();
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Film.class, id));
    }

    @Override
    public Film save(Film f) {
        if (f.getId() == null) {
            entityManager.persist(f);
            return f;
        } else {
            return entityManager.merge(f);
        }
    }

    @Override
    public void deleteById(Long id) {
        Film f = entityManager.find(Film.class, id);
        if (f != null) {
            entityManager.remove(f);
        }
    }

    @Override
    public List<Film> findByTitoloContainingIgnoreCase(String t) {
        return entityManager.createQuery("SELECT f FROM Film f WHERE LOWER(f.titolo) LIKE LOWER(:t)", Film.class)
                .setParameter("t", "%" + t + "%")
                .getResultList();
    }

    @Override
    public List<Film> findByTipologia(String t) {
        return entityManager.createQuery("SELECT f FROM Film f WHERE f.tipologia = :t", Film.class)
                .setParameter("t", t)
                .getResultList();
    }

    @Override
    public List<Film> findByAnno(Integer a) {
        return entityManager.createQuery("SELECT f FROM Film f WHERE f.anno = :a", Film.class)
                .setParameter("a", a)
                .getResultList();
    }

    @Override
    public List<Film> findAllByOrderByValutazioneDesc() {
        return entityManager.createQuery("SELECT f FROM Film f ORDER BY f.valutazione DESC", Film.class)
                .getResultList();
    }

    @Override
    public List<Film> findAllByOrderByAnnoDesc() {
        return entityManager.createQuery("SELECT f FROM Film f ORDER BY f.anno DESC", Film.class)
                .getResultList();
    }
}