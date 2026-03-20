package com.progetto.catalogo_film.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "film")
@Data
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titolo;

    @Column(columnDefinition = "TEXT")
    private String trama;

    private Integer anno;
    private Integer durata;
    private String posterUrl;
    private Double valutazione;

    @Column(length = 20)
    private String tipologia;

    @Column(unique = true)
    private Integer tmdbId;
    @Transient // Questo significa che NON viene creato nel DB, serve solo per passarlo al frontend
    private String trailerKey;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "film_generi",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "genere_id"))
    private List<Genere> generi;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "film_attori",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "attore_id"))
    private List<Attore> attori;

    @OneToMany(mappedBy = "film", fetch = FetchType.LAZY)
    private List<Recensione> recensioni;

    @OneToMany(mappedBy = "film", fetch = FetchType.LAZY)
    private List<ValutazioneStorica> valutazioniStoriche;
}