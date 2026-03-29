package com.progetto.catalogo_film.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "attori")
@Data
public class Attore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    private String fotoUrl;

    @Column(unique = true)
    private Integer tmdbId;
}