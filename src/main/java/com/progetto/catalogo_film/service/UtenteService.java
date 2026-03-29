package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.entity.Utente;
import com.progetto.catalogo_film.repository.UtenteRepository;
import com.progetto.catalogo_film.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtenteService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UtenteService(UtenteRepository utenteRepository,
                         PasswordEncoder passwordEncoder,
                         JwtService jwtService) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(String email, String password) {
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if (utente.getBannato()) {
            throw new RuntimeException("Utente bannato");
        }

        if (!passwordEncoder.matches(password, utente.getPassword())) {
            throw new RuntimeException("Password errata");
        }

        return jwtService.generaToken(email, utente.getRuolo().name(), utente.getUsername());
    }

    public Utente registra(String username, String email, String password) {
        if (utenteRepository.existsByEmail(email)) {
            throw new RuntimeException("Email già in uso");
        }
        if (utenteRepository.existsByUsername(username)) {
            throw new RuntimeException("Username già in uso");
        }

        Utente utente = new Utente();
        utente.setUsername(username);
        utente.setEmail(email);
        utente.setPassword(passwordEncoder.encode(password));
        utente.setRuolo(Utente.Ruolo.SPETTATORE);
        utente.setBannato(false);

        return utenteRepository.save(utente);
    }

    public Utente bannaUtente(Long id) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        utente.setBannato(true);
        return utenteRepository.save(utente);
    }

    public Utente sbannaUtente(Long id) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        utente.setBannato(false);
        return utenteRepository.save(utente);
    }

    public Utente promuoviARedattore(Long id) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        utente.setRuolo(Utente.Ruolo.REDATTORE);
        return utenteRepository.save(utente);
    }

    public List<Utente> getTuttiUtenti() {
        return utenteRepository.findAll();
    }

    public Utente getUtente(Long id) {
        return utenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    }

    public Utente getUtenteByEmail(String email) {
        return utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
    }

    public void eliminaUtente(Long id) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        utenteRepository.delete(utente);
    }

    public Utente aggiornaProfilo(String emailCorrente, String nuovoUsername, String nuovaPassword, String fotoBase64) {
        Utente utente = utenteRepository.findByEmail(emailCorrente)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        if (nuovoUsername != null && !nuovoUsername.isBlank()) {
            if (!utente.getUsername().equals(nuovoUsername) && utenteRepository.existsByUsername(nuovoUsername)) {
                throw new RuntimeException("Username già in uso da un altro utente");
            }
            utente.setUsername(nuovoUsername);
        }

        if (nuovaPassword != null && !nuovaPassword.isBlank()) {
            utente.setPassword(passwordEncoder.encode(nuovaPassword));
        }

        if (fotoBase64 != null && !fotoBase64.isBlank()) {
            utente.setFotoBase64(fotoBase64);
        }

        return utenteRepository.save(utente);
    }
}