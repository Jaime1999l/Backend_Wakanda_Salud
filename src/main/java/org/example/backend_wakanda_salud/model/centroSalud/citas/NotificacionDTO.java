package org.example.backend_wakanda_salud.model.centroSalud.citas;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class NotificacionDTO {

    private Long id;

    private Long usuarioId;

    @NotNull
    private String tipo;

    @NotNull
    private String mensaje;

    @NotNull
    private Date fechaHoraEnvio;

    @NotNull
    private Boolean leida;

    private Long referenciaCitaId;
}
