package org.example.backend_wakanda_salud.model.usuarios.pacientes.historialMedico;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.backend_wakanda_salud.domain.usuarios.pacientes.historialMedico.HistorialMedico;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class EntradaHistorialDTO {

    private Long id;

    @NotNull
    private Date fecha;

    @NotNull
    private String descripcion;

    private Long medicoId;

    private Long historialMedicoId;

    private List<String> prescripciones;

}
