package org.example.backend_wakanda_salud.domain.usuarios.medicos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "AgendasMedicas")
@Getter
@Setter
public class AgendaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @OneToMany(mappedBy = "agendaMedica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disponibilidad> horariosDisponibles;
}

