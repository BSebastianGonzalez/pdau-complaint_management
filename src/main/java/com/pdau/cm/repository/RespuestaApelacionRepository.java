package com.pdau.cm.repository;

import com.pdau.cm.model.RespuestaApelacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RespuestaApelacionRepository extends JpaRepository<RespuestaApelacion, Long> {
    boolean existsByApelacionId(Long apelacionId);
}
