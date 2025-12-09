package com.pdau.cm.controller;

import com.pdau.cm.dto.ApelacionDTO;
import com.pdau.cm.model.Apelacion;
import com.pdau.cm.service.ApelacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/apelaciones")
@RequiredArgsConstructor
public class ApelacionController {

    private final ApelacionService apelacionService;

    @PostMapping(consumes = "multipart/form-data")
    public Apelacion crear(
            @RequestPart("data") ApelacionDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> archivos
    ) throws Exception {
        return apelacionService.crearApelacion(dto, archivos);
    }

    @GetMapping("/denuncia/{denunciaId}")
    public Apelacion obtenerPorDenuncia(@PathVariable Long denunciaId) {
        return apelacionService.obtenerPorDenunciaId(denunciaId);
    }
}
