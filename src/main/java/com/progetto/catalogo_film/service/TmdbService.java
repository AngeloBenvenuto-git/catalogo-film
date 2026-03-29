package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.entity.Attore;
import com.progetto.catalogo_film.entity.Film;
import com.progetto.catalogo_film.entity.Genere;
import com.progetto.catalogo_film.repository.AttoreRepository;
import com.progetto.catalogo_film.repository.FilmRepository;
import com.progetto.catalogo_film.repository.GenereRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TmdbService {

    private final WebClient webClient;
    private final FilmRepository filmRepository;
    private final AttoreRepository attoreRepository;
    private final GenereRepository genereRepository;

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.url}")
    private String apiUrl;

    @Value("${tmdb.api.image.url}")
    private String imageUrl;

    public TmdbService(FilmRepository filmRepository,
                       AttoreRepository attoreRepository,
                       GenereRepository genereRepository) {
        this.webClient = WebClient.create();
        this.filmRepository = filmRepository;
        this.attoreRepository = attoreRepository;
        this.genereRepository = genereRepository;
    }

    public void importaFilmPopolare(int pagine) {
        if (filmRepository.count() > 0) return;

        importaGeneri();

        for (int pagina = 1; pagina <= pagine; pagina++) {
            Map response = webClient.get()
                    .uri(apiUrl + "/movie/popular?api_key=" + apiKey + "&language=it-IT&page=" + pagina)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) continue;

            List<Map> films = (List<Map>) response.get("results");
            for (Map filmData : films) {
                salvaFilm(filmData);
            }
        }
    }

    private void importaGeneri() {
        Map response = webClient.get()
                .uri(apiUrl + "/genre/movie/list?api_key=" + apiKey + "&language=it-IT")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null) return;

        List<Map> generi = (List<Map>) response.get("genres");
        for (Map genereData : generi) {
            Integer tmdbId = (Integer) genereData.get("id");
            if (!genereRepository.existsByTmdbId(tmdbId)) {
                Genere genere = new Genere();
                genere.setTmdbId(tmdbId);
                genere.setNome((String) genereData.get("name"));
                genereRepository.save(genere);
            }
        }
    }

    private void salvaFilm(Map filmData) {
        Integer tmdbId = (Integer) filmData.get("id");
        if (filmRepository.existsByTmdbId(tmdbId)) return;

        Film film = new Film();
        film.setTmdbId(tmdbId);
        film.setTitolo((String) filmData.get("title"));
        film.setTrama((String) filmData.get("overview"));
        film.setTipologia("FILM");

        String posterPath = (String) filmData.get("poster_path");
        if (posterPath != null) {
            film.setPosterUrl(imageUrl + posterPath);
        }

        Object voto = filmData.get("vote_average");
        if (voto instanceof Number) {
            film.setValutazione(((Number) voto).doubleValue());
        }

        String dataUscita = (String) filmData.get("release_date");
        if (dataUscita != null && dataUscita.length() >= 4) {
            film.setAnno(Integer.parseInt(dataUscita.substring(0, 4)));
        }

        Map dettagli = getDettagliFilm(tmdbId);
        if (dettagli != null) {
            Object durata = dettagli.get("runtime");
            if (durata instanceof Number) {
                film.setDurata(((Number) durata).intValue());
            }
        }

        List<Integer> genereIds = (List<Integer>) filmData.get("genre_ids");
        List<Genere> generi = new ArrayList<>();
        if (genereIds != null) {
            for (Integer genereId : genereIds) {
                genereRepository.findByTmdbId(genereId)
                        .ifPresent(generi::add);
            }
        }
        film.setGeneri(generi);

        filmRepository.save(film);
        importaAttori(film, tmdbId);
    }

    private void importaAttori(Film film, Integer tmdbId) {
        Map response = webClient.get()
                .uri(apiUrl + "/movie/" + tmdbId + "/credits?api_key=" + apiKey + "&language=it-IT")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null) return;

        List<Map> cast = (List<Map>) response.get("cast");
        if (cast == null) return;

        List<Attore> attori = new ArrayList<>();
        for (int i = 0; i < Math.min(5, cast.size()); i++) {
            Map attoreData = cast.get(i);
            Integer attoreId = (Integer) attoreData.get("id");

            Attore attore = attoreRepository.findByTmdbId(attoreId)
                    .orElseGet(() -> {
                        Attore nuovo = new Attore();
                        nuovo.setTmdbId(attoreId);
                        nuovo.setNome((String) attoreData.get("name"));
                        String profilePath = (String) attoreData.get("profile_path");
                        if (profilePath != null) {
                            nuovo.setFotoUrl(imageUrl + profilePath);
                        }
                        return attoreRepository.save(nuovo);
                    });
            attori.add(attore);
        }
        film.setAttori(attori);
        filmRepository.save(film);
    }

    private Map getDettagliFilm(Integer tmdbId) {
        return webClient.get()
                .uri(apiUrl + "/movie/" + tmdbId + "?api_key=" + apiKey + "&language=it-IT")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public String getTopMoviesByGenre(String genreName, int count) {
        List<Film> films = filmRepository.findAll().stream()
                .filter(f -> f.getGeneri().stream()
                        .anyMatch(g -> g.getNome().toLowerCase().contains(genreName.toLowerCase())))
                .limit(count)
                .toList();

        if (films.isEmpty()) {
            return "Al momento non ho film di genere " + genreName + " nel catalogo, ma ne caricherò presto di nuovi!";
        }

        Film consigliato = films.get(0);
        return "Ti consiglio di guardare '" + consigliato.getTitolo() + "' (" + consigliato.getAnno() + "). " +
                "Ha una valutazione di " + consigliato.getValutazione() + " stelle. Ti piace come idea?";
    }
}