package com.progetto.catalogo_film.config;

import com.progetto.catalogo_film.dao.FilmDAO; // Importiamo il nuovo DAO
import com.progetto.catalogo_film.dao.UtenteDAO; // Importiamo il nuovo DAO
import com.progetto.catalogo_film.entity.Utente;
import com.progetto.catalogo_film.service.TmdbService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UtenteDAO utenteDAO,
                           FilmDAO filmDAO,
                           TmdbService tmdbService,
                           PasswordEncoder passwordEncoder) {
        return args -> {

            // Verifichiamo se l'admin esiste usando il DAO manuale
            if (utenteDAO.findByEmail("admin@admin.com").isEmpty()) {
                Utente admin = new Utente();
                admin.setUsername("admin");
                admin.setEmail("admin@admin.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRuolo(Utente.Ruolo.ADMIN);
                admin.setBannato(false);

                utenteDAO.save(admin);
                System.out.println(">>> Sistema: Utente admin creato con successo.");
            }

            // Verifichiamo se ci sono film nel DB usando il DAO
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