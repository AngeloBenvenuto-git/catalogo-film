package com.progetto.catalogo_film.dto;

import com.progetto.catalogo_film.entity.Utente;
import java.time.LocalDateTime;

public class UtenteDTO {

    private Long id;
    private String username;
    private String email;
    private Utente.Ruolo ruolo;
    private Boolean bannato;
    private LocalDateTime dataRegistrazione;
    private String fotoBase64;

    public UtenteDTO(Utente utente) {
        this.id = utente.getId();
        this.username = utente.getUsername();
        this.email = utente.getEmail();
        this.ruolo = utente.getRuolo();
        this.bannato = utente.getBannato();
        this.dataRegistrazione = utente.getDataRegistrazione();
        this.fotoBase64 = utente.getFotoBase64();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Utente.Ruolo getRuolo() { return ruolo; }
    public Boolean getBannato() { return bannato; }
    public LocalDateTime getDataRegistrazione() { return dataRegistrazione; }
    public String getFotoBase64() { return fotoBase64; }
}