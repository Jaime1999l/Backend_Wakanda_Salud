package org.example.backend_wakanda_salud.model.centroSalud;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.backend_wakanda_salud.domain.centroSalud.SistemaCitas;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.Medico;
import org.example.backend_wakanda_salud.domain.usuarios.pacientes.Paciente;
import org.example.backend_wakanda_salud.model.usuarios.medicos.MedicoDTO;
import org.example.backend_wakanda_salud.model.usuarios.pacientes.PacienteDTO;

import java.util.List;

@Getter
@Setter
public class CentroSaludDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    private String nombre;

    @NotNull
    @Size(max = 255)
    private String direccion;

    @NotNull
    @Size(max = 255)
    private String telefono;

    private List<MedicoDTO> medicos;

    private List<PacienteDTO> pacientes;

    private List<SistemaCitasDTO> sistemasCitas;

}

