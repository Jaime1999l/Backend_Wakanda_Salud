package org.example.backend_wakanda_salud.model.centroSalud;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.backend_wakanda_salud.model.centroSalud.citas.CitaDTO;

import java.util.List;

@Getter
@Setter
public class SistemaCitasDTO {

    private Long id;

    @NotNull
    private String descripcion;

    private Long centroSaludId;

    private List<CitaDTO> citas;
}
