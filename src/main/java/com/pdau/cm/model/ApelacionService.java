package com.pdau.cm.model;

import com.pdau.cm.repository.ApelacionRepository;
import com.pdau.cm.repository.RespuestaRepository;
import lombok.RequiredArgsConstructor;
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

    @Value("${respuesta.appeal-days}")
    private int appealDays;

    public Apelacion registrarApelacion(
            Long denunciaId,
            String detalle,
            List<MultipartFile> files
    ) throws Exception {

        // 1. Buscar respuesta asociada
        Respuesta respuesta = respuestaRepository.findByDenunciaId(denunciaId)
                .orElseThrow(() -> new RuntimeException("No existe respuesta para esta denuncia"));

        // 2. Validar que no exista ya una apelación
        boolean yaExiste = apelacionRepository.existsByRespuestaId(respuesta.getId());
        if (yaExiste) {
            throw new RuntimeException("Esta denuncia ya fue apelada");
        }

        // 3. Validar límite de tiempo
        Date fechaLimite = Date.from(
                respuesta.getFechaRespuesta()
                        .toInstant()
                        .plus(appealDays, ChronoUnit.DAYS)
        );

        if (new Date().after(fechaLimite)) {
            throw new RuntimeException("El tiempo para apelar ha expirado");
        }

        // 4. Crear apelación base
        Apelacion apelacion = new Apelacion();
        apelacion.setDenunciaId(denunciaId);
        apelacion.setRespuesta(respuesta);
        apelacion.setDetalle(detalle);
        apelacion.setFechaApelacion(new Date());

        // 5. Subir archivos a Cloudinary
        List<ArchivoApelacion> archivos = new ArrayList<>();

        if (files != null) {
            for (MultipartFile file : files) {

                Map upload = cloudinaryService.upload(file);

                ArchivoApelacion archivo = new ArchivoApelacion();
                archivo.setNombre(file.getOriginalFilename());
                archivo.setTipoContenido(file.getContentType());
                archivo.setUrl((String) upload.get("secure_url"));
                archivo.setPublicId((String) upload.get("public_id"));
                archivo.setApelacion(apelacion);

                archivos.add(archivo);
            }
        }

        apelacion.setArchivos(archivos);

        // 6. Guardar en BD
        Apelacion saved = apelacionRepository.save(apelacion);

        // Si quieres luego emitir evento Rabbit, aquí sería el lugar

        return saved;
    }
}