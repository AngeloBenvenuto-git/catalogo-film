package com.progetto.catalogo_film.dto;

import com.progetto.catalogo_film.entity.Film;
import java.util.List;
import java.util.stream.Collectors;

public class FilmDTO {

    private Long id;
    private String titolo;
    private String trama;
    private Integer anno;
    private Integer durata;
    private String posterUrl;
    private Double valutazione;
    private String tipologia;
    private Integer tmdbId;
    private List<String> generi;
    private List<String> attori;

    public FilmDTO(Film f) {
        this.id = f.getId();
        this.titolo = f.getTitolo();
        this.trama = f.getTrama();
        this.anno = f.getAnno();
        this.durata = f.getDurata();
        this.posterUrl = f.getPosterUrl();
        this.valutazione = f.getValutazione();
        this.tipologia = f.getTipologia();
        this.tmdbId = f.getTmdbId();
        this.generi = f.getGeneri() != null
                ? f.getGeneri().stream().map(g -> g.getNome()).collect(Collectors.toList())
                : List.of();
        this.attori = f.getAttori() != null
                ? f.getAttori().stream().map(a -> a.getNome()).collect(Collectors.toList())
                : List.of();
    }

    public Long getId() { return id; }
    public String getTitolo() { return titolo; }
    public String getTrama() { return trama; }
    public Integer getAnno() { return anno; }
    public Integer getDurata() { return durata; }
    public String getPosterUrl() { return posterUrl; }
    public Double getValutazione() { return valutazione; }
    public String getTipologia() { return tipologia; }
    public Integer getTmdbId() { return tmdbId; }
    public List<String> getGeneri() { return generi; }
    public List<String> getAttori() { return attori; }
}