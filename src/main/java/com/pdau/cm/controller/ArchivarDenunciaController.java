package com.pdau.cm.controller;

import com.pdau.cm.model.ArchivamientoDenuncia;
import com.pdau.cm.service.ArchivarDenunciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/archivar")
@RequiredArgsConstructor
public class ArchivarDenunciaController {
    private final ArchivarDenunciaService archivarDenunciaService;

    @PostMapping
    public ResponseEntity<?> archivarDenuncia(
            @RequestParam Long denunciaId,
            @RequestParam Long adminId,
            @RequestParam String justificacion) {

        try {
            ArchivamientoDenuncia result = archivarDenunciaService.archivarDenuncia(denunciaId, adminId, justificacion);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al archivar la denuncia: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ArchivamientoDenuncia>> listarTodos() {
        return ResponseEntity.ok(archivarDenunciaService.obtenerTodos());
    }

    @GetMapping("/denuncia/{denunciaId}")
    public ResponseEntity<ArchivamientoDenuncia> obtenerPorDenuncia(@PathVariable Long denunciaId) {
        return archivarDenunciaService.obtenerPorDenuncia(denunciaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
