package com.pdau.cm.dto;

import com.pdau.cm.model.ResultadoApelacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RespuestaApelacionDTO {
    private Long apelacionId;
    private Long adminId;
    private String detalle;
    private ResultadoApelacion resultado;
}
