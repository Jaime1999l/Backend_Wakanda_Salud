package org.example.backend_wakanda_salud.domain.centroSalud.citas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.Medico;
import org.example.backend_wakanda_salud.domain.usuarios.pacientes.Paciente;

import java.util.Date;

@Entity
@Table(name = "Citas")
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_cita", discriminatorType = DiscriminatorType.STRING)
public abstract class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date fechaHora;

    @Column(nullable = false)
    private String estado; // 'PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'PROPUESTA', 'ACEPTADA', 'RECHAZADA'

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;
}
