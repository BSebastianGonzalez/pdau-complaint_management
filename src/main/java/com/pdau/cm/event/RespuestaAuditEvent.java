package com.pdau.cm.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaAuditEvent {

    private Long respuestaId;
    private Long denunciaId;
    private Long adminId;
    private String detalle;
    private Date fechaRespuesta;
}
