package com.progetto.catalogo_film.service;

import com.progetto.catalogo_film.entity.Messaggio;
import com.progetto.catalogo_film.repository.MessaggioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessaggioService {

    private final MessaggioRepository messaggioRepository;

    public MessaggioService(MessaggioRepository messaggioRepository) {
        this.messaggioRepository = messaggioRepository;
    }

    public List<Messaggio> getTuttiMessaggi() {
        return messaggioRepository.findAllByOrderByDataInvioDesc();
    }

    public Messaggio getMessaggioById(Long id) {
        return messaggioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Messaggio non trovato"));
    }

    public List<Messaggio> getMessaggiNonLetti() {
        return messaggioRepository.findByLettoOrderByDataInvioDesc(false);
    }

    public List<Messaggio> getMessaggiPerUtente(String username) {
        return messaggioRepository.findByMittente_UsernameOrderByDataInvioDesc(username);
    }

    public List<Messaggio> cercaPerOggetto(String oggetto) {
        return messaggioRepository.findByOggettoContainingIgnoreCaseOrderByDataInvioDesc(oggetto);
    }

    public Messaggio aggiungiMessaggio(Messaggio messaggio) {
        return messaggioRepository.save(messaggio);
    }

    public Messaggio segnaComeLetto(Long id) {
        Messaggio messaggio = getMessaggioById(id);
        messaggio.setLetto(true);
        return messaggioRepository.save(messaggio);
    }

    public Messaggio rispondiAlMessaggio(Long id, String testoRisposta) {
        Messaggio messaggio = getMessaggioById(id);
        messaggio.setRisposta(testoRisposta);
        messaggio.setDataRisposta(LocalDateTime.now());
        messaggio.setLetto(true);
        return messaggioRepository.save(messaggio);
    }

    public void cancellaMessaggio(Long id) {
        messaggioRepository.deleteById(id);
    }

    public long contaNonLetti() {
        return messaggioRepository.countByLetto(false);
    }
}