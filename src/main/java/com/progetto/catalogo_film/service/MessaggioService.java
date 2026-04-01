package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.dao.MessaggioDAO;
import com.progetto.catalogo_film.entity.Messaggio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MessaggioService {

    private final MessaggioDAO messaggioDAO;

    public MessaggioService(MessaggioDAO messaggioDAO) {
        this.messaggioDAO = messaggioDAO;
    }

    @Transactional(readOnly = true)
    public List<Messaggio> getTuttiMessaggi() {
        return messaggioDAO.findAllSorted();
    }

    @Transactional(readOnly = true)
    public Messaggio getMessaggioById(Long id) {
        return messaggioDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Messaggio con ID " + id + " non trovato"));
    }

    @Transactional(readOnly = true)
    public List<Messaggio> getMessaggiNonLetti() {
        return messaggioDAO.findByLetto(false);
    }

    @Transactional(readOnly = true)
    public List<Messaggio> getMessaggiPerUtente(String username) {
        return messaggioDAO.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<Messaggio> cercaPerOggetto(String oggetto) {
        if (oggetto == null || oggetto.trim().isEmpty()) {
            return messaggioDAO.findAllSorted();
        }
        return messaggioDAO.searchByOggetto(oggetto);
    }

    public Messaggio aggiungiMessaggio(Messaggio messaggio) {
        if (messaggio.getDataInvio() == null) {
            messaggio.setDataInvio(LocalDateTime.now());
        }
        return messaggioDAO.save(messaggio);
    }

    public Messaggio segnaComeLetto(Long id) {
        Messaggio messaggio = getMessaggioById(id);
        messaggio.setLetto(true);
        return messaggioDAO.save(messaggio);
    }

    public Messaggio rispondiAlMessaggio(Long id, String testoRisposta) {
        Messaggio messaggio = getMessaggioById(id);
        messaggio.setRisposta(testoRisposta);
        messaggio.setDataRisposta(LocalDateTime.now());
        messaggio.setLetto(true);
        return messaggioDAO.save(messaggio);
    }

    public void cancellaMessaggio(Long id) {
        getMessaggioById(id);
        messaggioDAO.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long contaNonLetti() {
        return messaggioDAO.countUnread(false);
    }
}