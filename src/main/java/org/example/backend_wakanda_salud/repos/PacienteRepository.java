package org.example.backend_wakanda_salud.repos;

import org.example.backend_wakanda_salud.domain.usuarios.pacientes.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // Encuentra un paciente por su número de historia clínica
    Optional<Paciente> findByNumeroHistoriaClinica(String numeroHistoriaClinica);
}

