package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.dao.FilmDAO;
import com.progetto.catalogo_film.dao.ListaCurataDAO;
import com.progetto.catalogo_film.dao.UtenteDAO;
import com.progetto.catalogo_film.entity.Film;
import com.progetto.catalogo_film.entity.ListaCurata;
import com.progetto.catalogo_film.entity.Utente;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional // Fondamentale per mantenere attiva la sessione JPA durante le operazioni sulle liste film
public class ListaCurataService {

    private final ListaCurataDAO listaCurataDAO;
    private final FilmDAO filmDAO;
    private final UtenteDAO utenteDAO;

    public ListaCurataService(ListaCurataDAO listaCurataDAO,
                              FilmDAO filmDAO,
                              UtenteDAO utenteDAO) {
        this.listaCurataDAO = listaCurataDAO;
        this.filmDAO = filmDAO;
        this.utenteDAO = utenteDAO;
    }

    @Transactional(readOnly = true)
    public List<ListaCurata> getTutteListe() {
        return listaCurataDAO.findAllSorted();
    }

    @Transactional(readOnly = true)
    public List<ListaCurata> getListeRedattore(String email) {
        Utente redattore = utenteDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato: " + email));
        return listaCurataDAO.findByRedattoreId(redattore.getId());
    }

    @Transactional(readOnly = true)
    public ListaCurata getListaById(Long id) {
        return listaCurataDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Lista non trovata con ID: " + id));
    }

    public ListaCurata creaLista(String email, String titolo, String descrizione) {
        Utente redattore = utenteDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (listaCurataDAO.existsByTitoloAndRedattoreId(titolo, redattore.getId())) {
            throw new RuntimeException("Esiste già una lista con questo titolo creata da te");
        }

        ListaCurata lista = new ListaCurata();
        lista.setTitolo(titolo);
        lista.setDescrizione(descrizione);
        lista.setRedattore(redattore);

        return listaCurataDAO.save(lista);
    }

    public ListaCurata aggiungiFilm(Long listaId, Long filmId, String email) {
        ListaCurata lista = getListaById(listaId);
        Utente utente = utenteDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Controllo permessi
        boolean isAdmin = utente.getRuolo().name().contains("ADMIN");
        boolean isOwner = lista.getRedattore().getId().equals(utente.getId());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("Accesso negato: non sei il proprietario della lista");
        }

        Film film = filmDAO.findById(filmId)
                .orElseThrow(() -> new RuntimeException("Film non trovato"));

        // Evita duplicati nella lista
        if (lista.getFilm().stream().anyMatch(f -> f.getId().equals(filmId))) {
            throw new RuntimeException("Il film '" + film.getTitolo() + "' è già presente in questa lista");
        }

        lista.getFilm().add(film);
        return listaCurataDAO.save(lista);
    }

    public ListaCurata rimuoviFilm(Long listaId, Long filmId, String email) {
        ListaCurata lista = getListaById(listaId);
        Utente utente = utenteDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        boolean isAdmin = utente.getRuolo().name().contains("ADMIN");
        boolean isOwner = lista.getRedattore().getId().equals(utente.getId());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("Accesso negato: non hai i permessi per modificare questa lista");
        }

        lista.getFilm().removeIf(f -> f.getId().equals(filmId));
        return listaCurataDAO.save(lista);
    }

    public void cancellaLista(Long id, String email) {
        ListaCurata lista = getListaById(id);
        Utente utente = utenteDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (!lista.getRedattore().getId().equals(utente.getId()) &&
                !utente.getRuolo().name().contains("ADMIN")) {
            throw new RuntimeException("Non hai i permessi per eliminare questa lista");
        }

        listaCurataDAO.deleteById(id);
    }

    public void aggiornaDatiLista(Long id, String nuovoTitolo, String nuovaDescrizione, String emailRichiedente) {
        ListaCurata lista = getListaById(id);
        Utente utenteRichiedente = utenteDAO.findByEmail(emailRichiedente)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        boolean isAdmin = utenteRichiedente.getRuolo().name().contains("ADMIN");
        boolean eIlProprietario = lista.getRedattore().getId().equals(utenteRichiedente.getId());

        if (!isAdmin && !eIlProprietario) {
            throw new RuntimeException("Non hai i permessi per modificare questa lista");
        }

        if (nuovoTitolo != null && !nuovoTitolo.trim().isEmpty()) {
            lista.setTitolo(nuovoTitolo);
        }
        if (nuovaDescrizione != null && !nuovaDescrizione.trim().isEmpty()) {
            lista.setDescrizione(nuovaDescrizione);
        }

        listaCurataDAO.save(lista);
    }

    public ListaCurata toggleLike(Long listaId, String email) {
        ListaCurata lista = getListaById(listaId);
        Utente utente = utenteDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Cerchiamo l'utente nella lista dei like
        boolean haGiaLiked = lista.getUtentiCheLike().stream()
                .anyMatch(u -> u.getId().equals(utente.getId()));

        if (haGiaLiked) {
            lista.getUtentiCheLike().removeIf(u -> u.getId().equals(utente.getId()));
        } else {
            lista.getUtentiCheLike().add(utente);
        }

        return listaCurataDAO.save(lista);
    }

    @Transactional(readOnly = true)
    public List<ListaCurata> getListeLikedDaUtente(String email) {
        Utente utente = utenteDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Filtriamo le liste in cui l'utente è presente nella collezione utentiCheLike
        return listaCurataDAO.findAll().stream()
                .filter(l -> l.getUtentiCheLike() != null && l.getUtentiCheLike().stream()
                        .anyMatch(u -> u.getId().equals(utente.getId())))
                .collect(Collectors.toList());
    }
}