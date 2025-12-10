package com.pdau.cm.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArchivamientoAuditEvent {

    private Long archivamientoId;
    private Long denunciaId;
    private Long adminId;
    private String justificacion;
    private LocalDateTime fechaArchivado;
}
