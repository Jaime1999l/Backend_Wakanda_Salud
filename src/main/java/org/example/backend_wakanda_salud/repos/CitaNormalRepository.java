package org.example.backend_wakanda_salud.repos;

import org.example.backend_wakanda_salud.domain.centroSalud.citas.CitaNormal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Repository
public interface CitaNormalRepository extends JpaRepository<CitaNormal, Long> {

    // Encuentra todas las citas normales por paciente ID
    List<CitaNormal> findByPaciente_Id(Long pacienteId);

    // Encuentra todas las citas normales por médico ID
    List<CitaNormal> findAllByMedico_Id(Long medicoId);

    List<CitaNormal> findAllByMedico_IdAndFechaHoraBetween(Long medicoId, Date horaInicio, Date horaFin);

    Page<CitaNormal> findByMedicoIdAndFechaHoraBetween(
            Long medicoId, Time horaInicio, Time horaFin, Pageable pageable);
}
