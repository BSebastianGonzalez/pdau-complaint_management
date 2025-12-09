package com.pdau.cm.repository;

import com.pdau.cm.model.RespuestaApelacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RespuestaApelacionRepository extends JpaRepository<RespuestaApelacion, Long> {
    boolean existsByApelacionId(Long apelacionId);
    Optional<RespuestaApelacion> findByApelacionDenunciaId(Long denunciaId);
}
