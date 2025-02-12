package org.example.backend_wakanda_salud.model.usuarios.medicos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DisponibilidadDTO {

    private Long id;

    @NotNull
    private Date fecha;

    @NotNull
    private Time horaInicio;

    @NotNull
    private Time horaFin;

    @NotNull
    private Boolean disponible;

    private Long agendaMedicaId;
}

