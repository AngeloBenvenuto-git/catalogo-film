package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.dao.FilmDAO;
import com.progetto.catalogo_film.entity.Film;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FilmService {

    private final FilmDAO filmDAO;

    public FilmService(FilmDAO filmDAO) {
        this.filmDAO = filmDAO;
    }

    @Transactional(readOnly = true)
    public List<Film> getTuttiFilm() {
        return filmDAO.findAll();
    }

    @Transactional(readOnly = true)
    public Film getFilmById(Long id) {
        return filmDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Film con ID " + id + " non trovato nel catalogo"));
    }

    @Transactional(readOnly = true)
    public List<Film> cercaPerTitolo(String titolo) {
        if (titolo == null || titolo.trim().isEmpty()) {
            return filmDAO.findAll();
        }
        return filmDAO.findByTitoloContainingIgnoreCase(titolo);
    }

    @Transactional(readOnly = true)
    public List<Film> cercaPerTipologia(String tipologia) {
        return filmDAO.findByTipologia(tipologia);
    }

    @Transactional(readOnly = true)
    public List<Film> cercaPerAnno(Integer anno) {
        return filmDAO.findByAnno(anno);
    }

    @Transactional(readOnly = true)
    public List<Film> ordinaPerValutazione() {
        return filmDAO.findAllByOrderByValutazioneDesc();
    }

    @Transactional(readOnly = true)
    public List<Film> ordinaPerAnno() {
        return filmDAO.findAllByOrderByAnnoDesc();
    }

    public Film aggiungiFilm(Film film) {
        return filmDAO.save(film);
    }

    public Film modificaFilm(Long id, Film filmModificato) {
        Film filmEsistente = getFilmById(id);

        filmEsistente.setTitolo(filmModificato.getTitolo());
        filmEsistente.setTrama(filmModificato.getTrama());
        filmEsistente.setAnno(filmModificato.getAnno());
        filmEsistente.setDurata(filmModificato.getDurata());
        filmEsistente.setTipologia(filmModificato.getTipologia());
        filmEsistente.setValutazione(filmModificato.getValutazione());

        if (filmModificato.getAttori() != null) filmEsistente.setAttori(filmModificato.getAttori());
        if (filmModificato.getGeneri() != null) filmEsistente.setGeneri(filmModificato.getGeneri());

        return filmDAO.save(filmEsistente);
    }

    public void cancellaFilm(Long id) {
        if (!filmDAO.findById(id).isPresent()) {
            throw new RuntimeException("Impossibile cancellare: Film non trovato");
        }
        filmDAO.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Film> cercaPerNomeGenere(String genere) {
        return filmDAO.findByGenere(genere);
    }
}