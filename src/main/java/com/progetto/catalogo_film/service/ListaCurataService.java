package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.entity.Film;
import com.progetto.catalogo_film.entity.ListaCurata;
import com.progetto.catalogo_film.entity.Utente;
import com.progetto.catalogo_film.repository.FilmRepository;
import com.progetto.catalogo_film.repository.ListaCurataRepository;
import com.progetto.catalogo_film.repository.UtenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListaCurataService {

    private final ListaCurataRepository listaCurataRepository;
    private final FilmRepository filmRepository;
    private final UtenteRepository utenteRepository;

    public ListaCurataService(ListaCurataRepository listaCurataRepository,
                              FilmRepository filmRepository,
                              UtenteRepository utenteRepository) {
        this.listaCurataRepository = listaCurataRepository;
        this.filmRepository = filmRepository;
        this.utenteRepository = utenteRepository;
    }

    public List<ListaCurata> getTutteListe() {
        return listaCurataRepository.findAllByOrderByDataCreazioneDesc();
    }

    public List<ListaCurata> getListeRedattore(String email) {
        Utente redattore = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        return listaCurataRepository.findByRedattoreIdOrderByDataCreazioneDesc(redattore.getId());
    }

    public ListaCurata getListaById(Long id) {
        return listaCurataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lista non trovata"));
    }

    public ListaCurata creaLista(String email, String titolo, String descrizione) {
        Utente redattore = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (listaCurataRepository.existsByTitoloAndRedattoreId(titolo, redattore.getId())) {
            throw new RuntimeException("Hai già una lista con questo titolo");
        }

        ListaCurata lista = new ListaCurata();
        lista.setTitolo(titolo);
        lista.setDescrizione(descrizione);
        lista.setRedattore(redattore);

        return listaCurataRepository.save(lista);
    }

    public ListaCurata aggiungiFilm(Long listaId, Long filmId, String email) {
        ListaCurata lista = getListaById(listaId);
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        boolean isAdmin = utente.getRuolo().name().contains("ADMIN");
        boolean isOwner = lista.getRedattore().getId().equals(utente.getId());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("Non hai i permessi per modificare questa lista");
        }

        Film film = filmRepository.findById(filmId)
                .orElseThrow(() -> new RuntimeException("Film non trovato"));

        if (lista.getFilm().stream().anyMatch(f -> f.getId().equals(filmId))) {
            throw new RuntimeException("Film già presente nella lista");
        }

        lista.getFilm().add(film);
        return listaCurataRepository.save(lista);
    }

    public ListaCurata rimuoviFilm(Long listaId, Long filmId, String email) {
        ListaCurata lista = getListaById(listaId);
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        boolean isAdmin = utente.getRuolo().name().contains("ADMIN");
        boolean isOwner = lista.getRedattore().getId().equals(utente.getId());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("Non hai i permessi per modificare questa lista");
        }

        lista.getFilm().removeIf(f -> f.getId().equals(filmId));
        return listaCurataRepository.save(lista);
    }

    public void cancellaLista(Long id, String email) {
        ListaCurata lista = getListaById(id);
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (!lista.getRedattore().getId().equals(utente.getId())
                && utente.getRuolo() != Utente.Ruolo.ADMIN) {
            throw new RuntimeException("Non puoi cancellare questa lista");
        }

        listaCurataRepository.deleteById(id);
    }

    public void aggiornaDatiLista(Long id, String nuovoTitolo, String nuovaDescrizione, String emailRichiedente) {
        ListaCurata lista = listaCurataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lista non trovata"));
        Utente utenteRichiedente = utenteRepository.findByEmail(emailRichiedente)
                .orElseThrow(() -> new RuntimeException("Utente non trovato nel database"));
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
        listaCurataRepository.save(lista);
    }
    public ListaCurata toggleLike(Long listaId, String email) {
        ListaCurata lista = getListaById(listaId);
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        boolean haGiaLiked = lista.getUtentiCheLike().stream()
                .anyMatch(u -> u.getId().equals(utente.getId()));

        if (haGiaLiked) {
            lista.getUtentiCheLike().removeIf(u -> u.getId().equals(utente.getId()));
        } else {
            lista.getUtentiCheLike().add(utente);
        }

        return listaCurataRepository.save(lista);
    }
    public List<ListaCurata> getListeLikedDaUtente(String email) {
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        return listaCurataRepository.findAll().stream()
                .filter(l -> l.getUtentiCheLike().stream()
                        .anyMatch(u -> u.getId().equals(utente.getId())))
                .collect(java.util.stream.Collectors.toList());
    }
}