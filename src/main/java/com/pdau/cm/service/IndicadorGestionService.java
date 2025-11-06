package com.pdau.cm.service;

import com.pdau.cm.model.IndicadorGestion;
import com.pdau.cm.model.Respuesta;
import com.pdau.cm.repository.IndicadorGestionRepository;
import com.pdau.cm.repository.RespuestaRepository;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class IndicadorGestionService {
    private final RespuestaRepository respuestaRepository;
    private final IndicadorGestionRepository indicadorRepository;
    private final EmailService emailService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${microservicios.registro-denuncias-url}")
    private String registroDenunciasUrl;

    @Value("${microservicios.auth-url}")
    private String authUrl;

    @Value("${sendgrid.api-key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    public IndicadorGestion generarIndicador() {
        // 1️⃣ Obtener denuncias desde el microservicio de registro
        String denunciasEndpoint = registroDenunciasUrl + "/api/denuncias/list";
        List<Map<String, Object>> denuncias = Arrays.asList(
                Objects.requireNonNull(
                        restTemplate.getForObject(denunciasEndpoint, Map[].class)
                )
        );

        // 2️⃣ Obtener respuestas locales
        List<Respuesta> respuestas = respuestaRepository.findAll();

        // Contadores
        int menor3 = 0, entre3y5 = 0, entre5y10 = 0, mayor10 = 0;

        // 3️⃣ Recorrer denuncias y buscar su respuesta
        for (Map<String, Object> denuncia : denuncias) {
            Long idDenuncia = ((Number) denuncia.get("id")).longValue();
            Date fechaCreacion = parseFecha(denuncia.get("fechaCreacion"));

            Optional<Respuesta> respuestaOpt = respuestas.stream()
                    .filter(r -> Objects.equals(r.getDenunciaId(), idDenuncia))
                    .findFirst();

            if (respuestaOpt.isPresent()) {
                Date fechaRespuesta = respuestaOpt.get().getFechaRespuesta();
                long dias = calcularDias(fechaCreacion, fechaRespuesta);

                if (dias < 3) menor3++;
                else if (dias <= 5) entre3y5++;
                else if (dias <= 10) entre5y10++;
                else mayor10++;
            }
        }

        int total = menor3 + entre3y5 + entre5y10 + mayor10;
        if (total == 0) total = 1; // evitar división por 0

        // 4️⃣ Calcular porcentajes
        double pMenor3 = (menor3 * 100.0) / total;
        double p3y5 = (entre3y5 * 100.0) / total;
        double p5y10 = (entre5y10 * 100.0) / total;
        double pMayor10 = (mayor10 * 100.0) / total;

        LocalDateTime ahora = LocalDateTime.now();

        // 5️⃣ Guardar indicador
        IndicadorGestion indicador = new IndicadorGestion();
        indicador.setFechaGeneracion(ahora);
        indicador.setMenor3Dias(menor3);
        indicador.setEntre3y5Dias(entre3y5);
        indicador.setEntre5y10Dias(entre5y10);
        indicador.setMayor10Dias(mayor10);
        indicador.setPorcentajeMenor3(pMenor3);
        indicador.setPorcentaje3y5(p3y5);
        indicador.setPorcentaje5y10(p5y10);
        indicador.setPorcentajeMayor10(pMayor10);

        indicadorRepository.save(indicador);

        if (pMayor10 > 50.0) {
            String correosEndpoint = authUrl + "/api/admin/especiales/correos";
            String[] correos = restTemplate.getForObject(correosEndpoint, String[].class);

            if (correos != null && correos.length > 0) {
                String subject = "⚠️ Alerta de retraso en respuestas";
                String htmlTemplate = """
    <div style='font-family: Arial, sans-serif; background-color: #f8f9fa; padding: 20px; border-radius: 8px; border: 1px solid #ddd; max-width: 600px; margin: auto;'>
        <h2 style='color:#d9534f; text-align:center;'>⚠️ Alerta de Retraso en Respuestas</h2>
        <hr style='border: none; border-top: 2px solid #d9534f; width: 60%%; margin: 10px auto;'>
        <p style='font-size: 16px; color: #333; text-align: justify;'>
            Estimado equipo,
        </p>
        <p style='font-size: 16px; color: #333; text-align: justify;'>
            Se ha detectado que más del <strong style='color:#d9534f;'>%s%%</strong> de las denuncias fueron respondidas después de 10 días desde su creación.
        </p>
        <div style='background-color:#fff3cd; border-left: 4px solid #ffc107; padding: 10px 15px; margin: 15px 0;'>
            <p style='margin: 0; font-size: 15px; color: #856404;'>
                ⚠️ Este es un indicador de posible retraso en la gestión de casos. 
                Se recomienda revisar los tiempos de respuesta y aplicar medidas correctivas.
            </p>
        </div>
        <p style='font-size: 15px; color: #555;'>
            📅 Fecha de generación del informe: <strong>%s</strong>
        </p>
        <p style='font-size: 14px; color: #777; text-align: center; margin-top: 30px;'>
            Sistema de Monitoreo de Denuncias<br>
            <span style='font-size: 13px;'>Este mensaje se generó automáticamente, por favor no responder.</span>
        </p>
    </div>
    """;

                String htmlContent = String.format(htmlTemplate, pMayor10, LocalDate.now());



                for (String correo : correos) {
                    emailService.sendEmail(correo, subject, htmlContent);
                }
            }
        }

        return indicador;
    }

    private Date parseFecha(Object value) {
        if (value instanceof String s) {
            try {
                return Date.from(LocalDate.parse(s.substring(0, 10))
                        .atStartOfDay(ZoneId.systemDefault()).toInstant());
            } catch (Exception ignored) {}
        }
        return null;
    }

    private long calcularDias(Date inicio, Date fin) {
        if (inicio == null || fin == null) return 0;
        return Duration.between(inicio.toInstant(), fin.toInstant()).toDays();
    }
}
