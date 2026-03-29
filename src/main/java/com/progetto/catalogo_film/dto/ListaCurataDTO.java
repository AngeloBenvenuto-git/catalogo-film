package com.progetto.catalogo_film.dto;

import com.progetto.catalogo_film.entity.ListaCurata;
import java.util.List;
import java.util.stream.Collectors;

public class ListaCurataDTO {

    private Long id;
    private String titolo;
    private String descrizione;
    private Long redattoreId;
    private String usernameRedattore;
    private List<Long> filmIds;
    private List<String> titoliFilm;
    private int numeroLike;
    private List<String> usernameCheLike;

    public ListaCurataDTO(ListaCurata l) {
        this.id = l.getId();
        this.titolo = l.getTitolo();
        this.descrizione = l.getDescrizione();
        this.redattoreId = l.getRedattore().getId();
        this.usernameRedattore = l.getRedattore().getUsername();
        this.filmIds = l.getFilm() != null
                ? l.getFilm().stream().map(f -> f.getId()).collect(Collectors.toList())
                : List.of();
        this.titoliFilm = l.getFilm() != null
                ? l.getFilm().stream().map(f -> f.getTitolo()).collect(Collectors.toList())
                : List.of();
        this.numeroLike = l.getUtentiCheLike() != null ? l.getUtentiCheLike().size() : 0;
        this.usernameCheLike = l.getUtentiCheLike() != null
                ? l.getUtentiCheLike().stream().map(u -> u.getUsername()).collect(Collectors.toList())
                : List.of();
    }

    public Long getId() { return id; }
    public String getTitolo() { return titolo; }
    public String getDescrizione() { return descrizione; }
    public Long getRedattoreId() { return redattoreId; }
    public String getUsernameRedattore() { return usernameRedattore; }
    public List<Long> getFilmIds() { return filmIds; }
    public List<String> getTitoliFilm() { return titoliFilm; }
    public int getNumeroLike() { return numeroLike; }
    public List<String> getUsernameCheLike() { return usernameCheLike; }
}