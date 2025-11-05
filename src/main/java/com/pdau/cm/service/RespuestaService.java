package com.pdau.cm.service;

import com.pdau.cm.dto.RespuestaRegistradaEvent;
import com.pdau.cm.model.ArchivoRespuesta;
import com.pdau.cm.model.Respuesta;
import com.pdau.cm.repository.RespuestaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RespuestaService {

    private final RespuestaRepository respuestaRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${respuesta.archivos.max-size}")
    private long maxFileSize;

    @Value("${respuesta.appeal-days}")
    private int appealDays;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    public Respuesta registrarRespuesta(Long denunciaId, Long adminId, String detalle, List<MultipartFile> files) throws Exception {

        // validar archivos
        List<ArchivoRespuesta> archivos = new ArrayList<>();
        if (files != null) {
            for (MultipartFile f : files) {
                if (f.getSize() > maxFileSize) {
                    throw new IllegalArgumentException("Archivo demasiado grande: " + f.getOriginalFilename());
                }
                ArchivoRespuesta ar = new ArchivoRespuesta();
                ar.setNombre(f.getOriginalFilename());
                ar.setTipoContenido(f.getContentType());
                ar.setDatos(f.getBytes());
                archivos.add(ar);
            }
        }

        Respuesta respuesta = new Respuesta();
        respuesta.setDenunciaId(denunciaId);
        respuesta.setAdminId(adminId);
        respuesta.setDetalle(detalle);
        respuesta.setFechaRespuesta(new Date());
        respuesta.setArchivos(archivos);

        // relacionar archivos con la respuesta antes de guardar
        archivos.forEach(a -> a.setRespuesta(respuesta));

        Respuesta saved = respuestaRepository.save(respuesta);

        // construir evento y enviarlo
        RespuestaRegistradaEvent event = new RespuestaRegistradaEvent(
                denunciaId,
                saved.getFechaRespuesta(),
                5L,
                appealDays,
                saved.getId()
        );

        rabbitTemplate.convertAndSend(exchange, routingKey, event);

        return saved;
    }
}
