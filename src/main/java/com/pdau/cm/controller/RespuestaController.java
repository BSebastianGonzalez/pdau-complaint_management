package com.pdau.cm.controller;

import com.pdau.cm.model.Respuesta;
import com.pdau.cm.service.RespuestaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/respuestas")
@RequiredArgsConstructor
public class RespuestaController {

    private final RespuestaService respuestaService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> crearRespuesta(
            @RequestParam("denunciaId") Long denunciaId,
            @RequestParam("adminId") Long adminId,
            @RequestParam("detalle") String detalle,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        try {
            Respuesta saved = respuestaService.registrarRespuesta(denunciaId, adminId, detalle, files);
            return ResponseEntity.status(201).body(saved);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al guardar la respuesta: " + e.getMessage());
        }
    }
}
