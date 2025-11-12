package com.pdau.cm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DenunciaDesarchivadaEvent {
    private Long denunciaId;
    private Long adminId;
    private String motivo;
    private LocalDateTime fechaDesarchivado;
}
