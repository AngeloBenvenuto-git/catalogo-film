package com.progetto.catalogo_film.security;

import com.progetto.catalogo_film.dao.UtenteDAO;
import com.progetto.catalogo_film.entity.Utente;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UtenteDAO utenteDAO;

    public UserDetailsServiceImpl(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utente utente = utenteDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + email));

        return User.builder()
                .username(utente.getEmail())
                .password(utente.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + utente.getRuolo().name())))
                .build();
    }
}