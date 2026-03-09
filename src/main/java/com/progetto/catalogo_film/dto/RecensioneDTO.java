package com.progetto.catalogo_film.dto;

import com.progetto.catalogo_film.entity.Recensione;
import java.time.LocalDateTime;

public class RecensioneDTO {

    private Long id;
    private String testo;
    private Integer voto;
    private LocalDateTime dataRecensione;
    private Long filmId;
    private String titoloFilm;
    private Long utenteId;
    private String usernameUtente;

    public RecensioneDTO(Recensione r) {
        this.id = r.getId();
        this.testo = r.getTesto();
        this.voto = r.getVoto();
        this.dataRecensione = r.getDataRecensione();
        this.filmId = r.getFilm().getId();
        this.titoloFilm = r.getFilm().getTitolo();
        this.utenteId = r.getUtente().getId();
        this.usernameUtente = r.getUtente().getUsername();
    }

    public Long getId() { return id; }
    public String getTesto() { return testo; }
    public Integer getVoto() { return voto; }
    public LocalDateTime getDataRecensione() { return dataRecensione; }
    public Long getFilmId() { return filmId; }
    public String getTitoloFilm() { return titoloFilm; }
    public Long getUtenteId() { return utenteId; }
    public String getUsernameUtente() { return usernameUtente; }
}