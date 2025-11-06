package com.pdau.cm.controller;

import com.pdau.cm.model.IndicadorGestion;
import com.pdau.cm.service.IndicadorGestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public List<IndicadorGestion> listarTodos() {
        return indicadorService.obtenerTodosIndicadores();
    }

    @GetMapping("/{id}")
    public IndicadorGestion obtenerPorId(@PathVariable Long id) {
        return indicadorService.obtenerIndicadorPorId(id);
    }
}
