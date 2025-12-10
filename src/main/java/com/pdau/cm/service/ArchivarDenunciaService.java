package com.pdau.cm.service;

import com.pdau.cm.config.RabbitConfig;
import com.pdau.cm.dto.DenunciaArchivadaEvent;
import com.pdau.cm.dto.DenunciaDesarchivadaEvent;
import com.pdau.cm.event.ArchivamientoAuditEvent;
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

        String url = authUrl + "/api/admin/" + adminId;
        Map<String, Object> adminData = restTemplate.getForObject(url, Map.class);
        String nombreAdmin = adminData != null
                ? (String) (adminData.get("nombre") + " " + adminData.get("apellido"))
                : "Administrador desconocido";

        ArchivamientoDenuncia archivamiento = new ArchivamientoDenuncia(
                denunciaId,
                adminId,
                justificacion,
                nombreAdmin,
                LocalDateTime.now()
        );

        // Guardar en BD (para obtener id)
        ArchivamientoDenuncia saved = archivamientoRepository.save(archivamiento);

        DenunciaArchivadaEvent dominioEvent = new DenunciaArchivadaEvent(
                denunciaId,
                adminId,
                justificacion,
                nombreAdmin,
                saved.getFechaArchivado()
        );
        rabbitTemplate.convertAndSend(
                RabbitConfig.ARCHIVAMIENTO_EXCHANGE,
                RabbitConfig.ARCHIVAMIENTO_ROUTING_KEY,
                dominioEvent
        );

        ArchivamientoAuditEvent auditEvent = new ArchivamientoAuditEvent();
        auditEvent.setArchivamientoId(saved.getId());
        auditEvent.setDenunciaId(saved.getDenunciaId());
        auditEvent.setAdminId(saved.getAdminId());
        auditEvent.setJustificacion(saved.getJustificacion());
        auditEvent.setFechaArchivado(saved.getFechaArchivado());

        rabbitTemplate.convertAndSend(
                RabbitConfig.ARCHIVAMIENTO_AUD_EXCHANGE,
                RabbitConfig.ARCHIVAMIENTO_AUD_ROUTING_KEY,
                auditEvent
        );

        return saved;
    }



    public List<ArchivamientoDenuncia> obtenerTodos() {
        return archivamientoRepository.findAll();
    }

    public Optional<ArchivamientoDenuncia> obtenerPorDenuncia(Long denunciaId) {
        return archivamientoRepository.findByDenunciaId(denunciaId);
    }

    public ArchivamientoDenuncia desarchivarDenuncia(Long denunciaId, Long adminId, String motivo) {
        System.out.println("🔓 Desarchivando denuncia: " + denunciaId);

        ArchivamientoDenuncia archivamiento = archivamientoRepository
                .findByDenunciaIdAndActivoTrue(denunciaId)
                .orElseThrow(() -> {
                    System.out.println("❌ No se encontró registro activo");
                    return new IllegalStateException("La denuncia no está archivada");
                });

        System.out.println("✅ Registro encontrado con activo=" + archivamiento.isActivo());

        // ✅ Cambiar a false
        archivamiento.setActivo(false);

        ArchivamientoDenuncia saved = archivamientoRepository.save(archivamiento);
        System.out.println("✅ Guardado con activo=" + saved.isActivo());

        // Enviar evento RabbitMQ
        DenunciaDesarchivadaEvent event = new DenunciaDesarchivadaEvent(
                denunciaId,
                adminId,
                motivo,
                LocalDateTime.now()
        );

        try {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.ARCHIVAMIENTO_EXCHANGE,
                    RabbitConfig.DESARCHIVAMIENTO_ROUTING_KEY,
                    event
            );
            System.out.println("✅ Evento enviado a RabbitMQ");
        } catch (Exception e) {
            System.err.println("⚠️ Error al enviar evento RabbitMQ: " + e.getMessage());
            // Continuar aunque falle RabbitMQ
        }

        return saved;
    }

}

