package com.pdau.cm.controller;

import com.pdau.cm.model.Apelacion;
import com.pdau.cm.model.ApelacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/apelaciones")
@RequiredArgsConstructor
public class ApelacionController {

    private final ApelacionService apelacionService;

    @PostMapping(value = "/registrar", consumes = "multipart/form-data")
    public Apelacion registrar(
            @RequestParam Long denunciaId,
            @RequestParam String detalle,
            @RequestParam(required = false) List<MultipartFile> files
    ) throws Exception {
        return apelacionService.registrarApelacion(denunciaId, detalle, files);
    }
}
