package org.example.backend_wakanda_salud.repos;

import org.example.backend_wakanda_salud.domain.centroSalud.citas.CitaUrgente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaUrgenteRepository extends JpaRepository<CitaUrgente, Long> {

    // Encuentra todas las citas urgentes por paciente ID
    List<CitaUrgente> findByPaciente_Id(Long pacienteId);

    // Encuentra todas las citas urgentes por médico ID
    List<CitaUrgente> findByMedico_Id(Long medicoId);

    // Encuentra citas urgentes por nivel de prioridad
    List<CitaUrgente> findByNivelPrioridad(String nivelPrioridad);

    // Encuentra citas urgentes entre fechas
    List<CitaUrgente> findByFechaHoraBetween(java.util.Date inicio, java.util.Date fin);
}
