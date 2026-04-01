package com.progetto.catalogo_film.entity;

import com.progetto.catalogo_film.dao.RecensioneDAO;
import java.util.List;

public class FilmProxy extends Film {
    private final RecensioneDAO recensioneDAO;
    private boolean recensioniCaricate = false;

    public FilmProxy(RecensioneDAO recensioneDAO) {
        this.recensioneDAO = recensioneDAO;
    }

    @Override
    public List<Recensione> getRecensioni() {
        if (!recensioniCaricate) {
            // Questo LOG è fondamentale per la demo con il prof!
            System.out.println(">>> [VIRTUAL PROXY] Accesso alle recensioni del film: " + this.getTitolo());

            // Carichiamo i dati tramite il DAO solo in questo istante
            super.setRecensioni(recensioneDAO.findByFilmId(this.getId()));
            recensioniCaricate = true;
        }
        return super.getRecensioni();
    }
}