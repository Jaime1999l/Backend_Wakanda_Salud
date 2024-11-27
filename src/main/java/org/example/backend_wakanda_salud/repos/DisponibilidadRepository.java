package org.example.backend_wakanda_salud.repos;

import org.example.backend_wakanda_salud.domain.usuarios.medicos.Disponibilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {

    // Encuentra disponibilidades por fecha
    List<Disponibilidad> findByFecha(Date fecha);

    // Encuentra disponibilidades por médico ID
    List<Disponibilidad> findByAgendaMedica_Medico_Id(Long medicoId);
}

