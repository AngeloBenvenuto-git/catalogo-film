package com.progetto.catalogo_film.config;

import com.progetto.catalogo_film.dao.FilmDAO;
import com.progetto.catalogo_film.service.TmdbService;
import com.progetto.catalogo_film.service.UtenteService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UtenteService utenteService,
                           FilmDAO filmDAO,
                           TmdbService tmdbService) {
        return args -> {

            utenteService.creaAdminIniziale("admin", "admin@admin.com", "admin123");

            // Controllo sui film
            if (filmDAO.findAll().isEmpty()) {
                System.out.println(">>> Sistema: Database vuoto, avvio importazione da TMDB...");
                tmdbService.importaFilmPopolare(5);
                System.out.println(">>> Sistema: Importazione TMDB completata.");
            } else {
                System.out.println(">>> Sistema: Film già presenti nel DB, importazione saltata.");
            }
        };
    }
}