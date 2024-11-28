package org.example.backend_wakanda_salud.service.citas;

import org.example.backend_wakanda_salud.domain.centroSalud.citas.Notificacion;
import org.example.backend_wakanda_salud.model.centroSalud.citas.NotificacionDTO;
import org.example.backend_wakanda_salud.repos.NotificacionRepository;
import org.example.backend_wakanda_salud.repos.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;

    public NotificacionService(NotificacionRepository notificacionRepository, UsuarioRepository usuarioRepository) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Crear una nueva notificación
    @Transactional
    public void enviarNotificacion(Long usuarioId, String tipo, String mensaje, Long referenciaCitaId) {
        // Verificar que el usuario exista
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // Crear la entidad de notificación
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTipo(tipo);
        notificacion.setMensaje(mensaje);
        notificacion.setFechaHoraEnvio(new Date());
        notificacion.setLeida(false);
        notificacion.setReferenciaCitaId(referenciaCitaId);

        // Guardar la notificación en la base de datos
        notificacionRepository.save(notificacion);
    }

    // Obtener todas las notificaciones de un usuario
    @Transactional(readOnly = true)
    public List<NotificacionDTO> obtenerNotificacionesDeUsuario(Long usuarioId) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        return notificacionRepository.findByUsuario_Id(usuario.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Métodos de mapeo entre entidad y DTO
    public NotificacionDTO mapToDTO(Notificacion notificacion) {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(notificacion.getId());
        dto.setUsuarioId(notificacion.getUsuario().getId());
        dto.setTipo(notificacion.getTipo());
        dto.setMensaje(notificacion.getMensaje());
        dto.setFechaHoraEnvio(notificacion.getFechaHoraEnvio());
        dto.setLeida(notificacion.getLeida());
        dto.setReferenciaCitaId(notificacion.getReferenciaCitaId());
        return dto;
    }
}

