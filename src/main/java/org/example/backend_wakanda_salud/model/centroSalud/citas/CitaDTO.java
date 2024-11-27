package org.example.backend_wakanda_salud.model.centroSalud.citas;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CitaDTO {

    private Long id;

    @NotNull
    private Date fechaHora;

    @NotNull
    private String estado;

    private Long pacienteId;

    private Long medicoId;
}

