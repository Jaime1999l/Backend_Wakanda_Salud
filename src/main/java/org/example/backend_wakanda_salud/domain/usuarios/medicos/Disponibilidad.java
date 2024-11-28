package org.example.backend_wakanda_salud.domain.usuarios.medicos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "Disponibilidades")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date fecha;

    @Column(nullable = false)
    private Time horaInicio;

    @Column(nullable = false)
    private Time horaFin;

    @Column(nullable = false)
    private Boolean disponible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_medica_id", nullable = false)
    private AgendaMedica agendaMedica;
}
