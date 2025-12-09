package com.pdau.cm.controller;

import com.pdau.cm.dto.RespuestaApelacionDTO;
import com.pdau.cm.model.RespuestaApelacion;
import com.pdau.cm.service.RespuestaApelacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/respuestas-apelacion")
@RequiredArgsConstructor
public class RespuestaApelacionController {

    private final RespuestaApelacionService service;

    @PostMapping(consumes = "multipart/form-data")
    public RespuestaApelacion responder(
            @RequestPart("data") RespuestaApelacionDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> archivos
    ) throws Exception {
        return service.responderApelacion(dto, archivos);
    }

    @GetMapping("/denuncia/{denunciaId}")
    public RespuestaApelacion obtenerPorDenuncia(@PathVariable Long denunciaId) {
        return service.obtenerPorDenunciaId(denunciaId);
    }
}