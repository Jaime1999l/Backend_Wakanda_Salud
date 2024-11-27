package org.example.backend_wakanda_salud.repos;

import org.example.backend_wakanda_salud.domain.centroSalud.SistemaCitas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SistemaCitasRepository extends JpaRepository<SistemaCitas, Long> {

    // Encuentra sistemas de citas por el ID del centro de salud
    List<SistemaCitas> findByCentroSalud_Id(Long centroSaludId);
}