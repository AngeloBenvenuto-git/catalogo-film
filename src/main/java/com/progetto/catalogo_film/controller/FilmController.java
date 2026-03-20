package com.progetto.catalogo_film.controller;

import com.progetto.catalogo_film.dto.FilmDTO;
import com.progetto.catalogo_film.entity.Film;
import com.progetto.catalogo_film.service.FilmService;
import com.progetto.catalogo_film.service.GoogleMapsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/film")
public class FilmController {

    private final FilmService filmService;
    private final GoogleMapsService googleMapsService;

    public FilmController(FilmService filmService,GoogleMapsService googleMapsService) {
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

    @GetMapping("/{id}")
    public ResponseEntity<FilmDTO> getFilmById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new FilmDTO(filmService.getFilmById(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cerca")
    public ResponseEntity<List<FilmDTO>> cerca(
            @RequestParam(required = false) String titolo,
            @RequestParam(required = false) String tipologia,
            @RequestParam(required = false) Integer anno,
            @RequestParam(required = false) Long genereId) {

        if (titolo != null) return ResponseEntity.ok(
                filmService.cercaPerTitolo(titolo).stream().map(FilmDTO::new).collect(Collectors.toList()));
        if (tipologia != null) return ResponseEntity.ok(
                filmService.cercaPerTipologia(tipologia).stream().map(FilmDTO::new).collect(Collectors.toList()));
        if (anno != null) return ResponseEntity.ok(
                filmService.cercaPerAnno(anno).stream().map(FilmDTO::new).collect(Collectors.toList()));
        if (genereId != null) return ResponseEntity.ok(
                filmService.cercaPerGenere(genereId).stream().map(FilmDTO::new).collect(Collectors.toList()));

        return ResponseEntity.ok(
                filmService.getTuttiFilm().stream().map(FilmDTO::new).collect(Collectors.toList()));
    }

    @GetMapping("/ordina/valutazione")
    public ResponseEntity<List<FilmDTO>> ordinaPerValutazione() {
        return ResponseEntity.ok(
                filmService.ordinaPerValutazione().stream().map(FilmDTO::new).collect(Collectors.toList())
        );
    }

    @GetMapping("/ordina/anno")
    public ResponseEntity<List<FilmDTO>> ordinaPerAnno() {
        return ResponseEntity.ok(
                filmService.ordinaPerAnno().stream().map(FilmDTO::new).collect(Collectors.toList())
        );
    }

    @PostMapping
    public ResponseEntity<FilmDTO> aggiungiFilm(@RequestBody Film film) {
        return ResponseEntity.ok(new FilmDTO(filmService.aggiungiFilm(film)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificaFilm(@PathVariable Long id, @RequestBody Film film) {
        try {
            return ResponseEntity.ok(new FilmDTO(filmService.modificaFilm(id, film)));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
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
            @RequestParam String citta) {
        try {
            filmService.getFilmById(id); // verifica che il film esista
            return ResponseEntity.ok(googleMapsService.trovaCinemaVicino(citta));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("errore", e.getMessage()));
        }
    }
}