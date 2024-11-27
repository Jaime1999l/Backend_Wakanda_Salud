package org.example.backend_wakanda_salud.model.usuarios;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UsuarioDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String nombre;

    @NotNull
    @Size(max = 255)
    private String apellidos;

    @NotNull
    @Size(max = 255)
    private String email;

    private List<String> roles;

    private Long credencialesId;
}
