package com.pdau.cm.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComentarioAuditEvent {

    private Long comentarioId;
    private Long denunciaId;
    private Long adminId;
    private String contenido;
    private LocalDateTime fechaCreacion;
}