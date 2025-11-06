package com.pdau.cm.controller;

import com.pdau.cm.model.IndicadorGestion;
import com.pdau.cm.service.IndicadorGestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/indicadores")
@RequiredArgsConstructor
public class IndicadorController {

    private final IndicadorGestionService indicadorService;

    @PostMapping("/generar")
    public ResponseEntity<IndicadorGestion> generarIndicador() {
        IndicadorGestion indicador = indicadorService.generarIndicador();
        return ResponseEntity.ok(indicador);
    }
}
