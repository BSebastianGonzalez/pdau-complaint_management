package com.pdau.cm.service;

import com.pdau.cm.config.RabbitConfig;
import com.pdau.cm.dto.DenunciaArchivadaEvent;
import com.pdau.cm.dto.DenunciaDesarchivadaEvent;
import com.pdau.cm.model.ArchivamientoDenuncia;
import com.pdau.cm.repository.ArchivamientoDenunciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArchivarDenunciaService {

    private final ArchivamientoDenunciaRepository archivamientoRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${microservicios.auth-url}")
    private String authUrl;

    public ArchivamientoDenuncia archivarDenuncia(Long denunciaId, Long adminId, String justificacion) {
        archivamientoRepository.findByDenunciaIdAndActivoTrue(denunciaId)
                .ifPresent(a -> { throw new IllegalStateException("La denuncia ya está archivada"); });

        // Obtener nombre del admin
        String url = authUrl + "/api/admin/" + adminId;
        Map<String, Object> adminData = restTemplate.getForObject(url, Map.class);
        String nombreAdmin = adminData != null
                ? adminData.get("nombre") + " " + adminData.get("apellido")
                : "Administrador desconocido";

        ArchivamientoDenuncia archivamiento = new ArchivamientoDenuncia();
        archivamiento.setDenunciaId(denunciaId);
        archivamiento.setAdminId(adminId);
        archivamiento.setJustificacion(justificacion);
        archivamiento.setNombreAdmin(nombreAdmin);
        archivamiento.setFechaArchivado(LocalDateTime.now());
        archivamiento.setActivo(true);

        archivamientoRepository.save(archivamiento);

        // Notificar vía RabbitMQ
        DenunciaArchivadaEvent event = new DenunciaArchivadaEvent(
                denunciaId,
                adminId,
                justificacion,
                nombreAdmin,
                archivamiento.getFechaArchivado()
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.ARCHIVAMIENTO_EXCHANGE,
                RabbitConfig.ARCHIVAMIENTO_ROUTING_KEY,
                event
        );

        return archivamiento;
    }


    public List<ArchivamientoDenuncia> obtenerTodos() {
        return archivamientoRepository.findAll();
    }

    public Optional<ArchivamientoDenuncia> obtenerPorDenuncia(Long denunciaId) {
        return archivamientoRepository.findByDenunciaId(denunciaId);
    }

    public ArchivamientoDenuncia desarchivarDenuncia(Long denunciaId, Long adminId, String motivo) {
        ArchivamientoDenuncia archivamiento = archivamientoRepository.findByDenunciaIdAndActivoTrue(denunciaId)
                .orElseThrow(() -> new IllegalStateException("La denuncia no está archivada"));

        archivamiento.setActivo(false);
        archivamientoRepository.save(archivamiento);

        // Enviar evento RabbitMQ para actualizar estado en el microservicio de denuncias
        DenunciaDesarchivadaEvent event = new DenunciaDesarchivadaEvent(
                denunciaId,
                adminId,
                motivo,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.ARCHIVAMIENTO_EXCHANGE,
                RabbitConfig.DESARCHIVAMIENTO_ROUTING_KEY,
                event
        );

        return archivamiento;
    }

}

