package org.example.backend_wakanda_salud.model.usuarios.medicos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.backend_wakanda_salud.model.usuarios.UsuarioDTO;

@Getter
@Setter
public class MedicoDTO extends UsuarioDTO {

    @NotNull
    @Size(max = 255)
    private String especialidad;

    @NotNull
    @Size(max = 255)
    private String numeroLicencia;

    private Long agendaId;

    private Long centroSaludId;
}
