package org.example.backend_wakanda_salud.repos;

import org.example.backend_wakanda_salud.domain.centroSalud.citas.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // Encuentra notificaciones por usuario ID
    List<Notificacion> findByUsuario_Id(Long usuarioId);

    // Encuentra notificaciones no leídas por usuario ID
    List<Notificacion> findByUsuario_IdAndLeidaFalse(Long usuarioId);
}

