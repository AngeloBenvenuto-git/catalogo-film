package com.progetto.catalogo_film.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "generi")
@Data
public class Genere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String nome;

    @Column(unique = true)
    private Integer tmdbId;
}