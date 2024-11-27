package org.example.backend_wakanda_salud.repos;

import org.example.backend_wakanda_salud.domain.usuarios.pacientes.historialMedico.HistorialMedico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HistorialMedicoRepository extends JpaRepository<HistorialMedico, Long> {

    // Encuentra un historial médico por paciente ID
    Optional<HistorialMedico> findByPaciente_Id(Long pacienteId);
}
