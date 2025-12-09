package com.pdau.cm.service;

import com.pdau.cm.config.RabbitConfig;
import com.pdau.cm.dto.RespuestaApelacionDTO;
import com.pdau.cm.event.RespuestaApelacionAuditEvent;
import com.pdau.cm.model.Apelacion;
import com.pdau.cm.model.ArchivoRespuestaApelacion;
import com.pdau.cm.model.CloudinaryService;
import com.pdau.cm.model.RespuestaApelacion;
import com.pdau.cm.repository.ApelacionRepository;
import com.pdau.cm.repository.RespuestaApelacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RespuestaApelacionService {

    private final RespuestaApelacionRepository repository;
    private final ApelacionRepository apelacionRepository;
    private final CloudinaryService cloudinaryService;
    private final RabbitTemplate rabbitTemplate;

    public RespuestaApelacion responderApelacion(
            RespuestaApelacionDTO dto,
            List<MultipartFile> archivos
    ) throws Exception {

        Apelacion apelacion = apelacionRepository.findById(dto.getApelacionId())
                .orElseThrow(() -> new RuntimeException("Apelación no encontrada"));

        if (repository.existsByApelacionId(apelacion.getId())) {
            throw new RuntimeException("Esta apelación ya fue respondida");
        }

        RespuestaApelacion resp = new RespuestaApelacion();
        resp.setApelacion(apelacion);
        resp.setAdminId(dto.getAdminId());
        resp.setDetalle(dto.getDetalle());
        resp.setFechaRespuesta(new Date());
        resp.setResultado(dto.getResultado());

        if (archivos != null) {
            for (MultipartFile file : archivos) {

                Map upload = cloudinaryService.upload(file);

                ArchivoRespuestaApelacion a = new ArchivoRespuestaApelacion();
                a.setNombre(file.getOriginalFilename());
                a.setTipoContenido(file.getContentType());
                a.setUrl((String) upload.get("secure_url"));
                a.setPublicId((String) upload.get("public_id"));
                a.setRespuestaApelacion(resp);

                resp.getArchivos().add(a);
            }
        }

        RespuestaApelacion saved = repository.save(resp);

        RespuestaApelacionAuditEvent event = new RespuestaApelacionAuditEvent();
        event.setRespuestaApelacionId(saved.getId());
        event.setApelacionId(saved.getApelacion().getId());
        event.setAdminId(saved.getAdminId());
        event.setDetalle(saved.getDetalle());
        event.setFechaRespuesta(saved.getFechaRespuesta());
        event.setResultado(saved.getResultado().name());

        rabbitTemplate.convertAndSend(
                RabbitConfig.RESPUESTA_APELACION_EXCHANGE,
                RabbitConfig.RESPUESTA_APELACION_ROUTING_KEY,
                event
        );

        return saved;
    }


    public RespuestaApelacion obtenerPorDenunciaId(Long denunciaId) {
        return repository.findByApelacionDenunciaId(denunciaId)
                .orElseThrow(() -> new RuntimeException("No existe respuesta de apelación para esa denuncia"));
    }
}