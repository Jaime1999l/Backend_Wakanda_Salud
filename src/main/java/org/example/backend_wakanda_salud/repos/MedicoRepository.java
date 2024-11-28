package org.example.backend_wakanda_salud.repos;

import org.example.backend_wakanda_salud.domain.usuarios.medicos.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    // Encuentra un médico por su especialidad
    List<Medico> findByEspecialidad(String especialidad);

    //Encuentra un médico por su id
    Optional<Medico> findById(Long id);
}

