package org.example.backend_wakanda_salud.domain.centroSalud.citas;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("NORMAL")
@Getter
@Setter
public class CitaNormal extends Cita {

    @Column
    private String motivo;

    @Column
    private Boolean creadaPorMedico; // Indica si fue generada por el médico
}
