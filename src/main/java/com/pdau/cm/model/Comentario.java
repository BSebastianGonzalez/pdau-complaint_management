package com.pdau.cm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idDenuncia;
    private Long idAdmin;
    private String contenido;
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Transient
    private String nombreAdmin;
}
