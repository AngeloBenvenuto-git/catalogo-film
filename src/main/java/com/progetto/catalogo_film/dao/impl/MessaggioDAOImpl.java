package com.progetto.catalogo_film.dao.impl;

import com.progetto.catalogo_film.dao.MessaggioDAO;
import com.progetto.catalogo_film.entity.Messaggio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class MessaggioDAOImpl implements MessaggioDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Messaggio> findAllSorted() {
        return entityManager.createQuery("SELECT m FROM Messaggio m ORDER BY m.dataInvio DESC", Messaggio.class)
                .getResultList();
    }

    @Override
    public List<Messaggio> findByLetto(Boolean letto) {
        return entityManager.createQuery("SELECT m FROM Messaggio m WHERE m.letto = :l ORDER BY m.dataInvio DESC", Messaggio.class)
                .setParameter("l", letto)
                .getResultList();
    }

    @Override
    public List<Messaggio> findByUsername(String username) {
        return entityManager.createQuery("SELECT m FROM Messaggio m WHERE m.mittente.username = :u ORDER BY m.dataInvio DESC", Messaggio.class)
                .setParameter("u", username)
                .getResultList();
    }

    @Override
    public List<Messaggio> searchByOggetto(String oggetto) {
        return entityManager.createQuery("SELECT m FROM Messaggio m WHERE LOWER(m.oggetto) LIKE LOWER(:o) ORDER BY m.dataInvio DESC", Messaggio.class)
                .setParameter("o", "%" + oggetto + "%")
                .getResultList();
    }

    @Override
    public long countUnread(Boolean letto) {
        return entityManager.createQuery("SELECT COUNT(m) FROM Messaggio m WHERE m.letto = :l", Long.class)
                .setParameter("l", letto)
                .getSingleResult();
    }

    @Override
    public Messaggio save(Messaggio m) {
        if (m.getId() == null) {
            entityManager.persist(m);
            return m;
        } else {
            return entityManager.merge(m);
        }
    }

    @Override
    public Optional<Messaggio> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Messaggio.class, id));
    }
}