package com.pdau.cm.controller;

import com.pdau.cm.model.Comentario;
import com.pdau.cm.service.ComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    @PostMapping
    public ResponseEntity<Comentario> crearComentario(@RequestBody Comentario comentario) {
        return ResponseEntity.ok(comentarioService.crearComentario(comentario));
    }

    @GetMapping("/denuncia/{idDenuncia}")
    public ResponseEntity<List<Comentario>> obtenerPorDenuncia(@PathVariable Long idDenuncia) {
        return ResponseEntity.ok(comentarioService.obtenerComentariosPorDenuncia(idDenuncia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarComentario(@PathVariable Long id) {
        try {
            comentarioService.eliminarComentario(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
