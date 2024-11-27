package org.example.backend_wakanda_salud.model.usuarios;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CredencialesDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String correo;

    @NotNull
    @Size(max = 255)
    private String password;

    private Long usuarioId;
}

