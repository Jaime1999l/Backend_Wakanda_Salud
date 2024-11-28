package org.example.backend_wakanda_salud.model.usuarios.pacientes;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.backend_wakanda_salud.model.usuarios.UsuarioDTO;

import java.util.Date;

@Getter
@Setter
public class PacienteDTO extends UsuarioDTO {

    @NotNull
    @Size(max = 255)
    private String numeroHistoriaClinica;

    @NotNull
    private Date fechaNacimiento;

    @NotNull
    @Size(max = 255)
    private String direccion;

    private Long historialMedicoId;
}
