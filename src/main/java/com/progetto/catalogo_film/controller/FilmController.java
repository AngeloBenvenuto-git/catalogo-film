package com.progetto.catalogo_film.controller;

import com.progetto.catalogo_film.dto.FilmDTO;
import com.progetto.catalogo_film.entity.Film;
import com.progetto.catalogo_film.service.FilmService;
import com.progetto.catalogo_film.service.GoogleMapsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/film")
public class FilmController {

    private final FilmService filmService;
    private final GoogleMapsService googleMapsService;

    public FilmController(FilmService filmService, GoogleMapsService googleMapsService) {
        this.filmService = filmService;
        this.googleMapsService = googleMapsService;
    }

    @GetMapping
    public ResponseEntity<List<FilmDTO>> getTuttiFilm() {
        return ResponseEntity.ok(
                filmService.getTuttiFilm()
                        .stream().map(FilmDTO::new).collect(Collectors.toList())
        );
    }

    @GetMapping("/cerca")
    public ResponseEntity<List<FilmDTO>> cerca(
            @RequestParam(required = false) String titolo,
            @RequestParam(required = false) String tipologia,
            @RequestParam(required = false) Integer anno,
            @RequestParam(required = false) String genere) {

        List<Film> risultati;

        if (titolo != null && !titolo.isBlank()) {
            risultati = filmService.cercaPerTitolo(titolo);
        } else if (genere != null && !genere.isBlank()) {
            risultati = filmService.cercaPerNomeGenere(genere);
        } else if (tipologia != null) {
            risultati = filmService.cercaPerTipologia(tipologia);
        } else if (anno != null) {
            risultati = filmService.cercaPerAnno(anno);
        } else {
            risultati = filmService.getTuttiFilm();
        }

        return ResponseEntity.ok(risultati.stream().map(FilmDTO::new).collect(Collectors.toList()));
    }

    @GetMapping("/ordina/valutazione")
    public ResponseEntity<List<FilmDTO>> ordinaPerValutazione() {
        return ResponseEntity.ok(filmService.ordinaPerValutazione().stream().map(FilmDTO::new).collect(Collectors.toList()));
    }

    @GetMapping("/ordina/anno")
    public ResponseEntity<List<FilmDTO>> ordinaPerAnno() {
        return ResponseEntity.ok(filmService.ordinaPerAnno().stream().map(FilmDTO::new).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFilmById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new FilmDTO(filmService.getFilmById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("errore", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> aggiungiFilm(@RequestBody Film film) {
        try {
            return ResponseEntity.ok(new FilmDTO(filmService.aggiungiFilm(film)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", "Errore aggiunta"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificaFilm(@PathVariable Long id, @RequestBody Film film) {
        try {
            return ResponseEntity.ok(new FilmDTO(filmService.modificaFilm(id, film)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancellaFilm(@PathVariable Long id) {
        filmService.cancellaFilm(id);
        return ResponseEntity.ok(Map.of("messaggio", "Film cancellato"));
    }

    @GetMapping("/{id}/cinema")
    public ResponseEntity<?> trovaCinemaVicino(
            @PathVariable Long id,
            @RequestParam double lat,
            @RequestParam double lng) {
        try {
            filmService.getFilmById(id);
            return ResponseEntity.ok(googleMapsService.trovaCinemaVicino(lat, lng));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }
}