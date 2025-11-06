package com.pdau.cm.controller;

import com.pdau.cm.model.ArchivoRespuesta;
import com.pdau.cm.model.Respuesta;
import com.pdau.cm.repository.ArchivoRespuestaRepository;
import com.pdau.cm.service.RespuestaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/respuestas")
@RequiredArgsConstructor
public class RespuestaController {

    private final RespuestaService respuestaService;
    private final ArchivoRespuestaRepository archivoRespuestaRepository;

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

    @GetMapping("/{denunciaId}")
    public ResponseEntity<Respuesta> getByDenunciaId(@PathVariable Long denunciaId) {
        return respuestaService.findByDenunciaId(denunciaId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{respuestaId}/archivos")
    public ResponseEntity<?> listarArchivosPorRespuesta(
            @PathVariable Long respuestaId,
            HttpServletRequest request
    ) {
        List<ArchivoRespuesta> archivos = archivoRespuestaRepository.findByRespuestaId(respuestaId);

        if (archivos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontraron archivos para esta respuesta.");
        }

        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");

        List<Map<String, Object>> archivosDTO = archivos.stream().map(a -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", a.getId());
            map.put("nombre", a.getNombre());
            map.put("tipoContenido", a.getTipoContenido());
            map.put("urlVisualizacion", baseUrl + "/api/respuestas/archivos/" + a.getId());
            return map;
        }).toList();

        return ResponseEntity.ok(archivosDTO);
    }
}
