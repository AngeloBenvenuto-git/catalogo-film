package com.progetto.catalogo_film.dao.impl;
import com.progetto.catalogo_film.dao.ValutazioneStoricaDAO;
import com.progetto.catalogo_film.entity.ValutazioneStorica;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ValutazioneStoricaDAOImpl implements ValutazioneStoricaDAO {
    @PersistenceContext private EntityManager entityManager;
    @Override public List<ValutazioneStorica> findAll() { return entityManager.createQuery("SELECT v FROM ValutazioneStorica v", ValutazioneStorica.class).getResultList(); }
    @Override public ValutazioneStorica save(ValutazioneStorica v) {
        if (v.getId() == null) { entityManager.persist(v); return v; }
        else { return entityManager.merge(v); }
    }
}