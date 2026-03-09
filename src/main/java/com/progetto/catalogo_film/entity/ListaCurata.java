package com.progetto.catalogo_film.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "liste_curate")
@Data
public class ListaCurata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titolo;

    @Column(columnDefinition = "TEXT")
    private String descrizione;

    @Column(name = "data_creazione")
    private LocalDateTime dataCreazione = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redattore_id", nullable = false)
    private Utente redattore;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "lista_film",
            joinColumns = @JoinColumn(name = "lista_id"),
            inverseJoinColumns = @JoinColumn(name = "film_id"))
    private List<Film> film;
}