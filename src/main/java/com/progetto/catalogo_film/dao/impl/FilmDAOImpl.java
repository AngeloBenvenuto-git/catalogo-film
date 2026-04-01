package com.progetto.catalogo_film.dao.impl;

import com.progetto.catalogo_film.dao.FilmDAO;
import com.progetto.catalogo_film.dao.RecensioneDAO;
import com.progetto.catalogo_film.entity.Film;
import com.progetto.catalogo_film.entity.FilmProxy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FilmDAOImpl implements FilmDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RecensioneDAO recensioneDAO;

    private Film convertToProxy(Film f) {
        if (f == null) return null;

        FilmProxy proxy = new FilmProxy(recensioneDAO);

        proxy.setId(f.getId());
        proxy.setTitolo(f.getTitolo());
        proxy.setTrama(f.getTrama());
        proxy.setAnno(f.getAnno());
        proxy.setTipologia(f.getTipologia());
        proxy.setPosterUrl(f.getPosterUrl());
        proxy.setValutazione(f.getValutazione());
        proxy.setDurata(f.getDurata());
        proxy.setTmdbId(f.getTmdbId());

        proxy.setGeneri(f.getGeneri());

        proxy.setAttori(f.getAttori());

        return proxy;
    }

    @Override
    public List<Film> findAll() {
        List<Film> risultati = entityManager.createQuery(
                "SELECT DISTINCT f FROM Film f LEFT JOIN FETCH f.generi",
                Film.class
        ).getResultList();
        return risultati.stream().map(this::convertToProxy).collect(Collectors.toList());
    }

    @Override
    public Optional<Film> findById(Long id) {
        List<Film> risultati = entityManager.createQuery(
                "SELECT f FROM Film f LEFT JOIN FETCH f.generi WHERE f.id = :id",
                Film.class
        ).setParameter("id", id).getResultList();
        return risultati.isEmpty() ? Optional.empty() : Optional.of(convertToProxy(risultati.get(0)));
    }

    @Override
    public List<Film> findByGenere(String genere) {
        String jpql = "SELECT DISTINCT f FROM Film f JOIN f.generi g WHERE LOWER(g.nome) = LOWER(:g)";
        List<Film> risultati = entityManager.createQuery(jpql, Film.class)
                .setParameter("g", genere.trim())
                .getResultList();
        return risultati.stream().map(this::convertToProxy).collect(Collectors.toList());
    }

    @Override
    public List<Film> findByTitoloContainingIgnoreCase(String t) {
        List<Film> risultati = entityManager.createQuery("SELECT f FROM Film f WHERE LOWER(f.titolo) LIKE LOWER(:t)", Film.class)
                .setParameter("t", "%" + t + "%")
                .getResultList();
        return risultati.stream().map(this::convertToProxy).collect(Collectors.toList());
    }

    @Override
    public List<Film> findByTipologia(String t) {
        List<Film> risultati = entityManager.createQuery("SELECT f FROM Film f WHERE f.tipologia = :t", Film.class)
                .setParameter("t", t)
                .getResultList();
        return risultati.stream().map(this::convertToProxy).collect(Collectors.toList());
    }

    @Override
    public List<Film> findByAnno(Integer a) {
        List<Film> risultati = entityManager.createQuery("SELECT f FROM Film f WHERE f.anno = :a", Film.class)
                .setParameter("a", a)
                .getResultList();
        return risultati.stream().map(this::convertToProxy).collect(Collectors.toList());
    }

    @Override
    public List<Film> findAllByOrderByValutazioneDesc() {
        List<Film> risultati = entityManager.createQuery("SELECT f FROM Film f ORDER BY f.valutazione DESC", Film.class)
                .getResultList();
        return risultati.stream().map(this::convertToProxy).collect(Collectors.toList());
    }

    @Override
    public List<Film> findAllByOrderByAnnoDesc() {
        List<Film> risultati = entityManager.createQuery("SELECT f FROM Film f ORDER BY f.anno DESC", Film.class)
                .getResultList();
        return risultati.stream().map(this::convertToProxy).collect(Collectors.toList());
    }

    @Override
    public Film save(Film f) {
        Film entityToSave = f;
        if (f instanceof com.progetto.catalogo_film.entity.FilmProxy) {
            entityToSave = new Film();
            entityToSave.setId(f.getId());
            entityToSave.setTitolo(f.getTitolo());
            entityToSave.setTrama(f.getTrama());
            entityToSave.setAnno(f.getAnno());
            entityToSave.setDurata(f.getDurata());
            entityToSave.setPosterUrl(f.getPosterUrl());
            entityToSave.setValutazione(f.getValutazione());
            entityToSave.setTipologia(f.getTipologia());
            entityToSave.setTmdbId(f.getTmdbId());
            entityToSave.setGeneri(f.getGeneri());
            entityToSave.setAttori(f.getAttori());
        }
        if (entityToSave.getId() == null) {
            entityManager.persist(entityToSave);
            return entityToSave;
        } else {
            return entityManager.merge(entityToSave);
        }
    }

    @Override
    public void deleteById(Long id) {
        Film f = entityManager.find(Film.class, id);
        if (f != null) {
            entityManager.remove(f);
        }
    }
}