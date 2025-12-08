package com.pdau.cm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArchivoRespuestaApelacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String tipoContenido;
    private String url;
    private String publicId;

    @ManyToOne
    @JoinColumn(name = "respuesta_apelacion_id")
    @JsonBackReference
    private RespuestaApelacion respuestaApelacion;
}
