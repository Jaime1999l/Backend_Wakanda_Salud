package org.example.backend_wakanda_salud.model.usuarios.pacientes.historialMedico;

import lombok.Getter;
import lombok.Setter;
import org.example.backend_wakanda_salud.domain.usuarios.pacientes.historialMedico.EntradaHistorial;

import java.util.List;

@Getter
@Setter
public class HistorialMedicoDTO {

    private Long id;

    private Long pacienteId;

    private List<EntradaHistorialDTO> entradas;
}

