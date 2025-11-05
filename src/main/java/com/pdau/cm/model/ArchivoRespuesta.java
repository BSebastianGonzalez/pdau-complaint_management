package com.pdau.cm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoRespuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String tipoContenido;
    private byte[] datos;

    @ManyToOne
    @JoinColumn(name = "respuesta_id")
    private Respuesta respuesta;
}
