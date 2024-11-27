package org.example.backend_wakanda_salud.repos;

import org.example.backend_wakanda_salud.domain.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Encuentra un usuario por su correo
    Optional<Usuario> findByEmail(String email);

    // Encuentra un usuario por su id de credenciales
    Optional<Usuario> findByCredenciales_Id(Long credencialesId);
}

