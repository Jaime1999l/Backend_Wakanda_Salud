package org.example.backend_wakanda_salud.repos;

import org.example.backend_wakanda_salud.domain.usuarios.Credenciales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredencialesRepository extends JpaRepository<Credenciales, Long> {

    // Encuentra credenciales por correo
    Optional<Credenciales> findByCorreo(String correo);

    // Encuentra credenciales por usuario_id
    Optional<Credenciales> findByUsuarioId(Long usuario_id);
}

