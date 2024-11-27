package org.example.backend_wakanda_salud.repos;

import org.example.backend_wakanda_salud.domain.centroSalud.citas.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    // Encuentra citas por paciente ID
    List<Cita> findByPaciente_Id(Long pacienteId);

    // Encuentra citas por médico ID
    List<Cita> findByMedico_Id(Long medicoId);

    // Encuentra citas entre fechas
    List<Cita> findByFechaHoraBetween(Date inicio, Date fin);
}

