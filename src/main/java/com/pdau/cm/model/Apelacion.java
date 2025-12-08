package com.pdau.cm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class Apelacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long denunciaId;

    @OneToOne
    @JoinColumn(name = "respuesta_id", unique = true)
    @JsonManagedReference
    private Respuesta respuesta;

    private String detalle;

    private Date fechaApelacion;

    @OneToMany(mappedBy = "apelacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ArchivoApelacion> archivos = new ArrayList<>();

    @OneToOne(mappedBy = "apelacion", cascade = CascadeType.ALL)
    @JsonBackReference
    private RespuestaApelacion respuestaApelacion;
}