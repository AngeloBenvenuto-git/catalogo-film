package com.progetto.catalogo_film.config;

import com.progetto.catalogo_film.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/liste/liked").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/liste/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/liste/*/like").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/liste/**").hasAnyRole("ADMIN", "REDATTORE")
                        .requestMatchers(HttpMethod.PUT, "/api/liste/**").hasAnyRole("ADMIN", "REDATTORE")
                        .requestMatchers(HttpMethod.DELETE, "/api/liste/**").hasAnyRole("ADMIN", "REDATTORE")
                        .requestMatchers(HttpMethod.GET, "/api/film/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/film/**").hasAnyRole("ADMIN", "REDATTORE")
                        .requestMatchers(HttpMethod.PUT, "/api/film/**").hasAnyRole("ADMIN", "REDATTORE")
                        .requestMatchers(HttpMethod.DELETE, "/api/film/**").hasRole("ADMIN")
                        .requestMatchers("/api/generi/**").permitAll()
                        .requestMatchers("/api/chat/**").permitAll()
                        .requestMatchers("/api/recensioni/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/favorites/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/favorites/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/favorites/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/messaggi/miei").authenticated() // <--- AGGIUNGI QUESTA!
                        .requestMatchers(HttpMethod.POST, "/api/messaggi/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/messaggi/**").hasAnyRole("ADMIN", "REDATTORE")
                        .requestMatchers(HttpMethod.PUT, "/api/messaggi/**").hasAnyRole("ADMIN", "REDATTORE")
                        .requestMatchers(HttpMethod.DELETE, "/api/messaggi/**").hasAnyRole("ADMIN", "REDATTORE")

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/redattore/**").hasAnyRole("REDATTORE", "ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}