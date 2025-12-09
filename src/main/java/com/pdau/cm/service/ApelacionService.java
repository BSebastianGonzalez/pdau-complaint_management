package com.pdau.cm.service;

import com.pdau.cm.config.RabbitConfig;
import com.pdau.cm.dto.ApelacionDTO;
import com.pdau.cm.event.ApelacionAuditEvent;
import com.pdau.cm.model.Apelacion;
import com.pdau.cm.model.ArchivoApelacion;
import com.pdau.cm.model.CloudinaryService;
import com.pdau.cm.model.Respuesta;
import com.pdau.cm.repository.ApelacionRepository;
import com.pdau.cm.repository.RespuestaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApelacionService {

    private final ApelacionRepository apelacionRepository;
    private final RespuestaRepository respuestaRepository;
    private final CloudinaryService cloudinaryService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${respuesta.appeal-days}")
    private int appealDays;

    public Apelacion crearApelacion(
            ApelacionDTO dto,
            List<MultipartFile> archivos
    ) throws Exception {

        Respuesta respuesta = respuestaRepository.findByDenunciaId(dto.getDenunciaId())
                .orElseThrow(() -> new RuntimeException("No existe respuesta para esta denuncia"));

        if (apelacionRepository.existsByRespuestaId(respuesta.getId())) {
            throw new RuntimeException("Ya existe una apelación para esta respuesta");
        }

        Date fechaLimite = Date.from(
                respuesta.getFechaRespuesta().toInstant().plus(appealDays, ChronoUnit.DAYS)
        );

        if (new Date().after(fechaLimite)) {
            throw new RuntimeException("El tiempo para apelar ha expirado");
        }

        Apelacion apelacion = new Apelacion();
        apelacion.setDenunciaId(dto.getDenunciaId());
        apelacion.setRespuesta(respuesta);
        apelacion.setDetalle(dto.getDetalle());
        apelacion.setFechaApelacion(new Date());

        List<ArchivoApelacion> lista = new ArrayList<>();
        if (archivos != null) {
            for (MultipartFile file : archivos) {

                Map upload = cloudinaryService.upload(file);

                ArchivoApelacion a = new ArchivoApelacion();
                a.setNombre(file.getOriginalFilename());
                a.setTipoContenido(file.getContentType());
                a.setUrl((String) upload.get("secure_url"));
                a.setPublicId((String) upload.get("public_id"));
                a.setApelacion(apelacion);

                lista.add(a);
            }
        }

        apelacion.setArchivos(lista);

        // ✅ Guardar
        Apelacion saved = apelacionRepository.save(apelacion);

        // ✅ Evento de auditoría
        ApelacionAuditEvent event = new ApelacionAuditEvent();
        event.setApelacionId(saved.getId());
        event.setDenunciaId(saved.getDenunciaId());
        event.setDetalle(saved.getDetalle());
        event.setFechaApelacion(saved.getFechaApelacion());

        rabbitTemplate.convertAndSend(
                RabbitConfig.APELACION_EXCHANGE,
                RabbitConfig.APELACION_ROUTING_KEY,
                event
        );

        return saved;
    }


    public Apelacion obtenerPorDenunciaId(Long denunciaId) {
        return apelacionRepository.findByDenunciaId(denunciaId)
                .orElseThrow(() -> new RuntimeException("No existe apelación para esta denuncia"));
    }
}
