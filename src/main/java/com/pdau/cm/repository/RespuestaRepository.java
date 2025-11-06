package com.pdau.cm.repository;

import com.pdau.cm.model.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
    Optional<Respuesta> findByDenunciaId(Long denunciaId);
}
