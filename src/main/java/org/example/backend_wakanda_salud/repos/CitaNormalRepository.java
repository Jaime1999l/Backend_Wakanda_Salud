package org.example.backend_wakanda_salud.repos;

import org.example.backend_wakanda_salud.domain.centroSalud.citas.CitaNormal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaNormalRepository extends JpaRepository<CitaNormal, Long> {

    // Encuentra todas las citas normales por paciente ID
    List<CitaNormal> findByPaciente_Id(Long pacienteId);

    // Encuentra todas las citas normales por médico ID
    List<CitaNormal> findByMedico_Id(Long medicoId);

    // Encuentra citas normales entre fechas
    List<CitaNormal> findByFechaHoraBetween(java.util.Date inicio, java.util.Date fin);
}

