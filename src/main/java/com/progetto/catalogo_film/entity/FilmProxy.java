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
            System.out.println(">>> [VIRTUAL PROXY] Accesso alle recensioni del film: " + this.getTitolo());
            super.setRecensioni(recensioneDAO.findByFilmId(this.getId()));
            recensioniCaricate = true;
        }
        return super.getRecensioni();
    }
    @Override
    public void setRecensioni(List<Recensione> recensioni) {
        super.setRecensioni(recensioni);
        this.recensioniCaricate = true;
    }
}