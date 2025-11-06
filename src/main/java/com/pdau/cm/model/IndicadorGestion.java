package com.pdau.cm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndicadorGestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime fechaGeneracion;

    private int menor3Dias;
    private int entre3y5Dias;
    private int entre5y10Dias;
    private int mayor10Dias;

    private double porcentajeMenor3;
    private double porcentaje3y5;
    private double porcentaje5y10;
    private double porcentajeMayor10;
}
