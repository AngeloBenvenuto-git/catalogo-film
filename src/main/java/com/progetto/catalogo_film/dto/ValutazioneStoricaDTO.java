package com.progetto.catalogo_film.dto;

import com.progetto.catalogo_film.entity.ValutazioneStorica;
import java.time.LocalDateTime;

public class ValutazioneStoricaDTO {

    private Long id;
    private Double vecchioVoto;
    private Double nuovoVoto;
    private LocalDateTime data;
    private Long filmId;
    private String titoloFilm;
    private Long redattoreId;
    private String usernameRedattore;

    public ValutazioneStoricaDTO(ValutazioneStorica v) {
        this.id = v.getId();
        this.vecchioVoto = v.getVecchioVoto();
        this.nuovoVoto = v.getNuovoVoto();
        this.data = v.getData();
        this.filmId = v.getFilm().getId();
        this.titoloFilm = v.getFilm().getTitolo();
        this.redattoreId = v.getRedattore().getId();
        this.usernameRedattore = v.getRedattore().getUsername();
    }

    public Long getId() { return id; }
    public Double getVecchioVoto() { return vecchioVoto; }
    public Double getNuovoVoto() { return nuovoVoto; }
    public LocalDateTime getData() { return data; }
    public Long getFilmId() { return filmId; }
    public String getTitoloFilm() { return titoloFilm; }
    public Long getRedattoreId() { return redattoreId; }
    public String getUsernameRedattore() { return usernameRedattore; }
}