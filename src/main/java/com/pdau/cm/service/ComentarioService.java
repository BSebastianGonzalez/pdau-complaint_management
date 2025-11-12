package com.pdau.cm.service;

import com.pdau.cm.model.Comentario;
import com.pdau.cm.repository.ComentarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final RestTemplate restTemplate;

    @Value("${microservicios.auth-url}")
    private String authenticationServiceUrl;

    public Comentario crearComentario(Comentario comentario) {
        comentario.setFechaCreacion(LocalDateTime.now());
        return comentarioRepository.save(comentario);
    }

    public List<Comentario> obtenerComentariosPorDenuncia(Long idDenuncia) {
        List<Comentario> comentarios = comentarioRepository.findByIdDenuncia(idDenuncia);

        comentarios.forEach(c -> {
            try {
                String url = authenticationServiceUrl + "/api/admin/" + c.getIdAdmin();
                Map<?, ?> admin = restTemplate.getForObject(url, Map.class);
                if (admin != null) {
                    c.setNombreAdmin((String) admin.get("nombre"));
                }
            } catch (Exception ignored) {
            }
        });

        return comentarios;
    }

    public void eliminarComentario(Long id) {
        if (!comentarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Comentario no encontrado con id: " + id);
        }
        comentarioRepository.deleteById(id);
    }
}
