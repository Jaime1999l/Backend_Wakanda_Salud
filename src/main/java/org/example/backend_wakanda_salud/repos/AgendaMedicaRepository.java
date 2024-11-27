package org.example.backend_wakanda_salud.repos;

import org.example.backend_wakanda_salud.domain.usuarios.medicos.AgendaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgendaMedicaRepository extends JpaRepository<AgendaMedica, Long> {

    // Encuentra una agenda médica por médico ID
    Optional<AgendaMedica> findByMedico_Id(Long medicoId);
}
