package org.example.backend_wakanda_salud.domain.centroSalud.citas;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("URGENTE")
@Getter
@Setter
public class CitaUrgente extends Cita {

    @Column(nullable = false)
    private String motivoUrgencia;

    @Column(nullable = false)
    private String nivelPrioridad; // 'ALTA', 'MEDIA', 'BAJA'
}
