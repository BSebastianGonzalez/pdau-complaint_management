package com.pdau.cm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class ArchivamientoDenuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long denunciaId;
    private Long adminId;
    private String justificacion;
    private String nombreAdmin;
    private LocalDateTime fechaArchivado;
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean activo = true;

    // ✅ Constructor para crear nuevos archivamientos
    public ArchivamientoDenuncia(Long denunciaId, Long adminId, String justificacion,
                                 String nombreAdmin, LocalDateTime fechaArchivado) {
        this.denunciaId = denunciaId;
        this.adminId = adminId;
        this.justificacion = justificacion;
        this.nombreAdmin = nombreAdmin;
        this.fechaArchivado = fechaArchivado;
        this.activo = true;
    }
}
