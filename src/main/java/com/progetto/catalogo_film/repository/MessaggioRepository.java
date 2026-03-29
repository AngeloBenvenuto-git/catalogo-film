package com.progetto.catalogo_film.repository;

import com.progetto.catalogo_film.entity.Messaggio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessaggioRepository extends JpaRepository<Messaggio, Long> {

    List<Messaggio> findAllByOrderByDataInvioDesc();

    List<Messaggio> findByLettoOrderByDataInvioDesc(Boolean letto);

    List<Messaggio> findByMittente_UsernameOrderByDataInvioDesc(String username);

    List<Messaggio> findByOggettoContainingIgnoreCaseOrderByDataInvioDesc(String oggetto);

    long countByLetto(Boolean letto);
}