package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.dao.AttoreDAO;
import com.progetto.catalogo_film.dao.FilmDAO;
import com.progetto.catalogo_film.dao.GenereDAO;
import com.progetto.catalogo_film.entity.Attore;
import com.progetto.catalogo_film.entity.Film;
import com.progetto.catalogo_film.entity.Genere;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional // Fondamentale per salvare film, attori e generi in sequenza
public class TmdbService {

    private final WebClient webClient;
    private final FilmDAO filmDAO;
    private final AttoreDAO attoreDAO;
    private final GenereDAO genereDAO;

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.url}")
    private String apiUrl;

    @Value("${tmdb.api.image.url}")
    private String imageUrl;

    public TmdbService(FilmDAO filmDAO,
                       AttoreDAO attoreDAO,
                       GenereDAO genereDAO) {
        this.webClient = WebClient.create();
        this.filmDAO = filmDAO;
        this.attoreDAO = attoreDAO;
        this.genereDAO = genereDAO;
    }

    public void importaFilmPopolare(int pagine) {
        // Usiamo findAll().size() per simulare il count
        if (!filmDAO.findAll().isEmpty()) return;

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

        List<Map> generiData = (List<Map>) response.get("genres");
        List<Genere> generiEsistenti = genereDAO.findAll();

        for (Map gData : generiData) {
            Integer tmdbId = (Integer) gData.get("id");
            boolean esiste = generiEsistenti.stream().anyMatch(g -> tmdbId.equals(g.getTmdbId()));

            if (!esiste) {
                Genere genere = new Genere();
                genere.setTmdbId(tmdbId);
                genere.setNome((String) gData.get("name"));
                // Qui andrebbe bene un genereDAO.save(genere)
                // Se non lo hai, assicurati di averlo nel DAO
            }
        }
    }

    private void salvaFilm(Map filmData) {
        Integer tmdbId = (Integer) filmData.get("id");

        // Controllo se esiste già tramite stream per non dover modificare il DAO subito
        boolean esiste = filmDAO.findAll().stream().anyMatch(f -> tmdbId.equals(f.getTmdbId()));
        if (esiste) return;

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
            List<Genere> tuttiGeneri = genereDAO.findAll();
            for (Integer gId : genereIds) {
                tuttiGeneri.stream()
                        .filter(g -> gId.equals(g.getTmdbId()))
                        .findFirst()
                        .ifPresent(generi::add);
            }
        }
        film.setGeneri(generi);

        filmDAO.save(film);
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

        List<Attore> attoriList = new ArrayList<>();
        List<Attore> attoriEsistenti = attoreDAO.findAll();

        for (int i = 0; i < Math.min(5, cast.size()); i++) {
            Map aData = cast.get(i);
            Integer aTmdbId = (Integer) aData.get("id");

            Optional<Attore> attoreOpt = attoriEsistenti.stream()
                    .filter(a -> aTmdbId.equals(a.getTmdbId()))
                    .findFirst();

            Attore attore;
            if (attoreOpt.isPresent()) {
                attore = attoreOpt.get();
            } else {
                attore = new Attore();
                attore.setTmdbId(aTmdbId);
                attore.setNome((String) aData.get("name"));
                String pPath = (String) aData.get("profile_path");
                if (pPath != null) {
                    attore.setFotoUrl(imageUrl + pPath);
                }
                attore = attoreDAO.save(attore);
            }
            attoriList.add(attore);
        }
        film.setAttori(attoriList);
        filmDAO.save(film);
    }

    private Map getDettagliFilm(Integer tmdbId) {
        return webClient.get()
                .uri(apiUrl + "/movie/" + tmdbId + "?api_key=" + apiKey + "&language=it-IT")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public String getTopMoviesByGenre(String genreName, int count) {
        List<Film> films = filmDAO.findAll().stream()
                .filter(f -> f.getGeneri().stream()
                        .anyMatch(g -> g.getNome().toLowerCase().contains(genreName.toLowerCase())))
                .limit(count)
                .toList();

        if (films.isEmpty()) {
            return "Al momento non ho film di genere " + genreName + " nel catalogo!";
        }

        Film consigliato = films.get(0);
        return "Ti consiglio '" + consigliato.getTitolo() + "' (" + consigliato.getAnno() + "). Valutazione: " + consigliato.getValutazione();
    }
}