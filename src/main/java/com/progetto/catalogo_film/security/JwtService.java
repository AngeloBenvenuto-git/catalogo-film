package com.progetto.catalogo_film.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generaToken(String email, String ruolo) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("ruolo", ruolo);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String estraiEmail(String token) {
        return estraiClaims(token).getSubject();
    }

    public String estraiRuolo(String token) {
        return estraiClaims(token).get("ruolo", String.class);
    }

    public boolean isTokenValido(String token, String email) {
        return estraiEmail(token).equals(email) && !isTokenScaduto(token);
    }

    private boolean isTokenScaduto(String token) {
        return estraiClaims(token).getExpiration().before(new Date());
    }

    private Claims estraiClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}