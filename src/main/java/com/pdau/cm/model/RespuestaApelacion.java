package com.pdau.cm.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaApelacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "apelacion_id", unique = true)
    @JsonManagedReference
    private Apelacion apelacion;

    private Long adminId;
    private String detalle;
    private Date fechaRespuesta;

    private ResultadoApelacion resultado;

    @OneToMany(mappedBy = "respuestaApelacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ArchivoRespuestaApelacion> archivos = new ArrayList<>();
}
