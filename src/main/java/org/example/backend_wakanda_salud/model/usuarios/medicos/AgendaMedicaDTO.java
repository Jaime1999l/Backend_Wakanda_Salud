package org.example.backend_wakanda_salud.model.usuarios.medicos;

import lombok.Getter;
import lombok.Setter;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.Disponibilidad;

import java.util.List;

@Getter
@Setter
public class AgendaMedicaDTO {

    private Long id;

    private Long medicoId;

    private List<DisponibilidadDTO> horariosDisponibles;
}
