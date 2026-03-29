package com.progetto.catalogo_film.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "valutazioni_storiche")
@Data
public class ValutazioneStorica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double vecchioVoto;

    @Column(nullable = false)
    private Double nuovoVoto;

    @Column(nullable = false)
    private LocalDateTime data = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", nullable = false)
    private Film film;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redattore_id", nullable = false)
    private Utente redattore;
}