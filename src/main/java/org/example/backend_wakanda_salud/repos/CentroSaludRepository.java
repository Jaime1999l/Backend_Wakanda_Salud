package org.example.backend_wakanda_salud.repos;

import org.example.backend_wakanda_salud.domain.centroSalud.CentroSalud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CentroSaludRepository extends JpaRepository<CentroSalud, Long> {

    // Encuentra un centro de salud por su nombre
    Optional<CentroSalud> findByNombre(String nombre);
}
