package com.progetto.catalogo_film.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "utenti")
@Data
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Ruolo ruolo;

    @Column(nullable = false)
    private Boolean bannato = false;

    @Column(name = "data_registrazione")
    private LocalDateTime dataRegistrazione = LocalDateTime.now();

    // --- AGGIUNTO: Supporto per la foto profilo in Base64 ---
    @Lob
    @Column(name = "foto_base64", columnDefinition = "LONGTEXT")
    private String fotoBase64;
    // -------------------------------------------------------

    @OneToMany(mappedBy = "utente", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recensione> recensioni;

    @OneToMany(mappedBy = "redattore", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListaCurata> listeCurate;

    public enum Ruolo {
        SPETTATORE, REDATTORE, ADMIN
    }

}