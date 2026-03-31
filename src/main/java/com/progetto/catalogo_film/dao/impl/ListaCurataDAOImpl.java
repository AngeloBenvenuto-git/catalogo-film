package com.progetto.catalogo_film.dao.impl;

import com.progetto.catalogo_film.dao.ListaCurataDAO;
import com.progetto.catalogo_film.entity.ListaCurata;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class ListaCurataDAOImpl implements ListaCurataDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ListaCurata> findAll() {
        return entityManager.createQuery("SELECT l FROM ListaCurata l", ListaCurata.class).getResultList();
    }

    @Override
    public List<ListaCurata> findAllSorted() {
        return entityManager.createQuery("SELECT l FROM ListaCurata l ORDER BY l.dataCreazione DESC", ListaCurata.class).getResultList();
    }

    @Override
    public List<ListaCurata> findByRedattoreId(Long id) {
        return entityManager.createQuery("SELECT l FROM ListaCurata l WHERE l.redattore.id = :id ORDER BY l.dataCreazione DESC", ListaCurata.class)
                .setParameter("id", id)
                .getResultList();
    }

    @Override
    public Optional<ListaCurata> findById(Long id) {
        return Optional.ofNullable(entityManager.find(ListaCurata.class, id));
    }

    @Override
    public ListaCurata save(ListaCurata l) {
        if (l.getId() == null) {
            entityManager.persist(l);
            return l;
        } else {
            return entityManager.merge(l);
        }
    }

    @Override
    public void deleteById(Long id) {
        ListaCurata l = entityManager.find(ListaCurata.class, id);
        if (l != null) {
            entityManager.remove(l);
        }
    }

    @Override
    public boolean existsByTitoloAndRedattoreId(String t, Long id) {
        Long count = entityManager.createQuery("SELECT COUNT(l) FROM ListaCurata l WHERE l.titolo = :t AND l.redattore.id = :id", Long.class)
                .setParameter("t", t)
                .setParameter("id", id)
                .getSingleResult();
        return count > 0;
    }
}