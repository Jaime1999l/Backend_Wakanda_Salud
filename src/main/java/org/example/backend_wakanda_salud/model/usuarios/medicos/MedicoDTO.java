package org.example.backend_wakanda_salud.model.usuarios.medicos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicoDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String especialidad;

    @NotNull
    @Size(max = 255)
    private String numeroLicencia;

    private Long usuarioId;

    private Long agendaId;
}
