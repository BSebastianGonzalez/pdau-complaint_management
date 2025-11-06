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
public class ArchivoRespuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String tipoContenido;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] datos;

    @ManyToOne
    @JoinColumn(name = "respuesta_id")
    @JsonBackReference
    private Respuesta respuesta;
}
