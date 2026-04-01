package com.progetto.catalogo_film.dao.impl;
import com.progetto.catalogo_film.dao.GenereDAO;
import com.progetto.catalogo_film.entity.Genere;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class GenereDAOImpl implements GenereDAO {
    @PersistenceContext private EntityManager entityManager;
    @Override public List<Genere> findAll() { return entityManager.createQuery("SELECT g FROM Genere g", Genere.class).getResultList(); }
    @Override public Optional<Genere> findByNome(String n) {
        try { return Optional.of(entityManager.createQuery("SELECT g FROM Genere g WHERE g.nome = :n", Genere.class).setParameter("n", n).getSingleResult()); }
        catch (Exception e) { return Optional.empty(); }
    }
    @Override
    public Optional<Genere> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Genere.class, id));
    }

    @Override
    public Genere save(Genere genere) {
        if (genere.getId() == null) {
            entityManager.persist(genere);
            return genere;
        } else {
            return entityManager.merge(genere);
        }
    }
}