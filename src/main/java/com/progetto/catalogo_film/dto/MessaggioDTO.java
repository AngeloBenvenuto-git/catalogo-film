package com.progetto.catalogo_film.dto;

import com.progetto.catalogo_film.entity.Messaggio;
import java.time.LocalDateTime;

public class MessaggioDTO {

    private Long id;
    private String mittenteUsername;
    private String mittenteEmail;
    private String oggetto;
    private String testo;
    private LocalDateTime dataInvio;
    private Boolean letto;
    private String risposta;
    private LocalDateTime dataRisposta;

    public MessaggioDTO(Messaggio m) {
        this.id = m.getId();
        this.mittenteUsername = m.getMittente() != null ? m.getMittente().getUsername() : null;
        this.mittenteEmail = m.getMittente() != null ? m.getMittente().getEmail() : null;
        this.oggetto = m.getOggetto();
        this.testo = m.getTesto();
        this.dataInvio = m.getDataInvio();
        this.letto = m.getLetto();
        this.risposta = m.getRisposta();
        this.dataRisposta = m.getDataRisposta();
    }

    public Long getId() { return id; }
    public String getMittenteUsername() { return mittenteUsername; }
    public String getMittenteEmail() { return mittenteEmail; }
    public String getOggetto() { return oggetto; }
    public String getTesto() { return testo; }
    public LocalDateTime getDataInvio() { return dataInvio; }
    public Boolean getLetto() { return letto; }
    public String getRisposta() { return risposta; }
    public LocalDateTime getDataRisposta() { return dataRisposta; }
}