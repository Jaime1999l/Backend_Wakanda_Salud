package org.example.backend_wakanda_salud.repos;

import org.example.backend_wakanda_salud.domain.usuarios.pacientes.historialMedico.EntradaHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntradaHistorialRepository extends JpaRepository<EntradaHistorial, Long> {

    // Encuentra entradas de historial por el ID del historial médico
    List<EntradaHistorial> findByHistorialMedico_Id(Long historialMedicoId);
}

