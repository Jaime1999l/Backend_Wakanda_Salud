package org.example.backend_wakanda_salud.model.centroSalud;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SistemaCitasDTO {

    private Long id;

    @NotNull
    private String descripcion;

    private Long centroSaludId;
}
