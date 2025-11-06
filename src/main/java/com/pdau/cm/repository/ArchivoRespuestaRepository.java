package com.pdau.cm.repository;

import com.pdau.cm.model.ArchivoRespuesta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArchivoRespuestaRepository extends JpaRepository<ArchivoRespuesta, Long> {
    List<ArchivoRespuesta> findByRespuestaId(Long respuestaId);
}
