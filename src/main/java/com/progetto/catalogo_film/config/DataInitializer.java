package com.progetto.catalogo_film.config;

import com.progetto.catalogo_film.entity.Utente;
import com.progetto.catalogo_film.repository.UtenteRepository;
import com.progetto.catalogo_film.service.TmdbService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UtenteRepository utenteRepository,
                           TmdbService tmdbService,
                           PasswordEncoder passwordEncoder,
                           com.progetto.catalogo_film.repository.FilmRepository filmRepository) {
        return args -> {

            if (!utenteRepository.existsByEmail("admin@admin.com")) {
                Utente admin = new Utente();
                admin.setUsername("admin");
                admin.setEmail("admin@admin.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRuolo(Utente.Ruolo.ADMIN);
                admin.setBannato(false);
                utenteRepository.save(admin);
                System.out.println("Utente admin creato");
            }

            if (filmRepository.count() == 0) {
                tmdbService.importaFilmPopolare(5);
                System.out.println("Importazione TMDB completata");
            } else {
                System.out.println("Film già presenti nel db, importazione saltata");
            }
        };
    }
}