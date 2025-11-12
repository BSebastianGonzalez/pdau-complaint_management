package com.pdau.cm.repository;

import com.pdau.cm.model.ArchivamientoDenuncia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArchivamientoDenunciaRepository extends JpaRepository<ArchivamientoDenuncia,Long> {
    Optional<ArchivamientoDenuncia> findByDenunciaId(Long denunciaId);
    Optional<ArchivamientoDenuncia> findByDenunciaIdAndActivoTrue(Long denunciaId);
}
