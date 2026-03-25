package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.entity.Favorite;
import com.progetto.catalogo_film.repository.FavoriteRepository;
import org.springframework.transaction.annotation.Transactional; // Assicurati sia questo import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional // Mettendolo qui, proteggi tutti i metodi della classe, specialmente le cancellazioni
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    public List<Favorite> getFavorites(String username) {
        return favoriteRepository.findByUsername(username);
    }

    public Object addFavorite(String username, Long filmId) {
        System.out.println("Service: Tento di aggiungere film " + filmId + " per utente " + username);

        // Controlliamo se esiste già usando l'Optional che abbiamo aggiunto nel Repository
        return favoriteRepository.findByUsernameAndFilmId(username, filmId)
                .map(existing -> {
                    System.out.println("Service: Film già presente, restituisco l'esistente.");
                    return (Object) existing;
                })
                .orElseGet(() -> {
                    System.out.println("Service: Nuovo preferito, procedo al salvataggio.");
                    Favorite fav = new Favorite();
                    fav.setUsername(username);
                    fav.setFilmId(filmId);
                    return favoriteRepository.save(fav);
                });
    }

    public void removeFavorite(String username, Long filmId) {
        System.out.println("Service: Rimuovo film " + filmId + " per utente " + username);
        favoriteRepository.deleteByUsernameAndFilmId(username, filmId);
    }
}

