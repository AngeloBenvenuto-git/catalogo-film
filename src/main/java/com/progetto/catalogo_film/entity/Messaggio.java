package com.progetto.catalogo_film.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "messaggi")
@Data
public class Messaggio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mittente_id", nullable = false)
    private Utente mittente;

    @Column(nullable = false, length = 255)
    private String oggetto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String testo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataInvio;

    @Column(nullable = false)
    private Boolean letto = false;

    @Column(columnDefinition = "TEXT")
    private String risposta;

    private LocalDateTime dataRisposta;

    @PrePersist
    protected void onCreate() {
        this.dataInvio = LocalDateTime.now();
    }
}