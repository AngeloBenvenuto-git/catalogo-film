package com.progetto.catalogo_film.dao.impl;

import com.progetto.catalogo_film.dao.FavoriteDAO;
import com.progetto.catalogo_film.entity.Favorite;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class FavoriteDAOImpl implements FavoriteDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Favorite> findByUsername(String username) {
        return entityManager.createQuery("SELECT f FROM Favorite f WHERE f.utente.username = :u", Favorite.class)
                .setParameter("u", username)
                .getResultList();
    }

    @Override
    public Optional<Favorite> findByUsernameAndFilmId(String username, Long filmId) {
        try {
            Favorite f = entityManager.createQuery(
                            "SELECT f FROM Favorite f WHERE f.utente.username = :u AND f.film.id = :fId", Favorite.class)
                    .setParameter("u", username)
                    .setParameter("fId", filmId)
                    .getSingleResult();
            return Optional.of(f);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteByUsernameAndFilmId(String username, Long filmId) {
        entityManager.createQuery("DELETE FROM Favorite f WHERE f.utente.username = :u AND f.film.id = :fId")
                .setParameter("u", username)
                .setParameter("fId", filmId)
                .executeUpdate();
    }

    @Override
    public Favorite save(Favorite favorite) {
        if (favorite.getId() == null) {
            entityManager.persist(favorite);
            return favorite;
        } else {
            return entityManager.merge(favorite);
        }
    }
}