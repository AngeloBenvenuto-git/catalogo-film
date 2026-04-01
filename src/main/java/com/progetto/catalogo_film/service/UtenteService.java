package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.dao.UtenteDAO;
import com.progetto.catalogo_film.entity.Utente;
import com.progetto.catalogo_film.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UtenteService {

    private final UtenteDAO utenteDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UtenteService(UtenteDAO utenteDAO,
                         PasswordEncoder passwordEncoder,
                         JwtService jwtService) {
        this.utenteDAO = utenteDAO;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public String login(String email, String password) {
        Utente utente = utenteDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con email: " + email));

        if (utente.getBannato() != null && utente.getBannato()) {
            throw new RuntimeException("Accesso negato: l'account è stato bannato");
        }

        if (!passwordEncoder.matches(password, utente.getPassword())) {
            throw new RuntimeException("Credenziali errate: password non valida");
        }

        return jwtService.generaToken(email, utente.getRuolo().name(), utente.getUsername());
    }

    public Utente registra(String username, String email, String password) {
        if (utenteDAO.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email già in uso nel sistema");
        }
        if (utenteDAO.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username già occupato");
        }

        Utente utente = new Utente();
        utente.setUsername(username);
        utente.setEmail(email);
        utente.setPassword(passwordEncoder.encode(password));

        utente.setRuolo(Utente.Ruolo.SPETTATORE);
        utente.setBannato(false);

        return utenteDAO.save(utente);
    }

    public Utente bannaUtente(Long id) {
        Utente utente = getUtente(id);
        utente.setBannato(true);
        return utenteDAO.save(utente);
    }

    public Utente sbannaUtente(Long id) {
        Utente utente = getUtente(id);
        utente.setBannato(false);
        return utenteDAO.save(utente);
    }

    public Utente promuoviARedattore(Long id) {
        Utente utente = getUtente(id);
        utente.setRuolo(Utente.Ruolo.REDATTORE);
        return utenteDAO.save(utente);
    }

    @Transactional(readOnly = true)
    public List<Utente> getTuttiUtenti() {
        return utenteDAO.findAll();
    }

    @Transactional(readOnly = true)
    public Utente getUtente(Long id) {
        return utenteDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Utente getUtenteByEmail(String email) {
        return utenteDAO.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nessun utente associato a questa email"));
    }

    public void eliminaUtente(Long id) {
        if (!utenteDAO.findById(id).isPresent()) {
            throw new RuntimeException("Impossibile eliminare: Utente non trovato");
        }
        Utente u = getUtente(id);
    }

    public Utente aggiornaProfilo(String emailCorrente, String nuovoUsername, String nuovaPassword, String fotoBase64) {
        Utente utente = getUtenteByEmail(emailCorrente);

        if (nuovoUsername != null && !nuovoUsername.isBlank()) {
            boolean usernameOccupato = utenteDAO.findByUsername(nuovoUsername)
                    .map(u -> !u.getEmail().equals(emailCorrente))
                    .orElse(false);

            if (usernameOccupato) {
                throw new RuntimeException("Lo username '" + nuovoUsername + "' è già in uso");
            }
            utente.setUsername(nuovoUsername);
        }

        if (nuovaPassword != null && !nuovaPassword.isBlank()) {
            utente.setPassword(passwordEncoder.encode(nuovaPassword));
        }

        if (fotoBase64 != null && !fotoBase64.isBlank()) {
            utente.setFotoBase64(fotoBase64);
        }

        return utenteDAO.save(utente);
    }
}