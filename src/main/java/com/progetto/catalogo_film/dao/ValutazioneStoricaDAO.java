package com.progetto.catalogo_film.dao;
import com.progetto.catalogo_film.entity.ValutazioneStorica;
import java.util.List;

public interface ValutazioneStoricaDAO {
    List<ValutazioneStorica> findAll();
    ValutazioneStorica save(ValutazioneStorica valutazione);
}