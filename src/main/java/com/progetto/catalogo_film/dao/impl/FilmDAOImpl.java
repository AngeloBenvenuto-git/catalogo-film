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

    /**
     * Trasforma un Film persistente in un FilmProxy per gestire
     * il caricamento Lazy manuale delle recensioni.
     */
    private Film convertToProxy(Film f) {
        if (f == null) return null;

        // Creiamo il proxy passandogli il DAO delle recensioni
        FilmProxy proxy = new FilmProxy(recensioneDAO);

        // Copiamo i campi semplici
        proxy.setId(f.getId());
        proxy.setTitolo(f.getTitolo());
        proxy.setTrama(f.getTrama());
        proxy.setAnno(f.getAnno());
        proxy.setTipologia(f.getTipologia());
        proxy.setPosterUrl(f.getPosterUrl());
        proxy.setValutazione(f.getValutazione());
        proxy.setDurata(f.getDurata());
        proxy.setTmdbId(f.getTmdbId());

        // FONDAMENTALE: Copiamo i generi altrimenti i filtri frontend non funzionano
        proxy.setGeneri(f.getGeneri());

        // Copiamo anche gli attori se servono nella lista
        proxy.setAttori(f.getAttori());

        // NOTA: NON settiamo le recensioni. Sarà il Proxy a caricarle
        // tramite getRecensioni() solo quando richiesto.
        return proxy;
    }

    @Override
    public List<Film> findAll() {
        List<Film> risultati = entityManager.createQuery("SELECT f FROM Film f", Film.class).getResultList();
        return risultati.stream().map(this::convertToProxy).collect(Collectors.toList());
    }

    @Override
    public Optional<Film> findById(Long id) {
        Film f = entityManager.find(Film.class, id);
        return Optional.ofNullable(convertToProxy(f));
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
}