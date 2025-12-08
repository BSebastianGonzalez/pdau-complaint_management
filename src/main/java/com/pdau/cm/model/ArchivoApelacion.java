package com.pdau.cm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoApelacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String tipoContenido;

    private String url;

    private String publicId;

    @ManyToOne
    @JoinColumn(name = "apelacion_id")
    @JsonBackReference
    private Apelacion apelacion;
}