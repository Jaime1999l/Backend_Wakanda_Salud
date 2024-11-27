package org.example.backend_wakanda_salud.model.centroSalud.citas;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CitaUrgenteDTO extends CitaDTO {

    @NotNull
    private String motivoUrgencia;

    @NotNull
    private String nivelPrioridad;
}
