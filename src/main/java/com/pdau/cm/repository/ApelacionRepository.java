package com.pdau.cm.repository;

import com.pdau.cm.model.Apelacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApelacionRepository extends JpaRepository<Apelacion, Long> {

    boolean existsByRespuestaId(Long respuestaId);

    Optional<Apelacion> findByDenunciaId(Long denunciaId);
}