package org.example.backend_wakanda_salud.domain.usuarios.pacientes.historialMedico;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.Medico;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "EntradasHistorial")
@Getter
@Setter
public class EntradaHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date fecha;

    @Column(nullable = false)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @ElementCollection
    @CollectionTable(name = "prescripciones", joinColumns = @JoinColumn(name = "entrada_historial_id"))
    @Column(name = "prescripcion")
    private List<String> prescripciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historial_medico_id", nullable = false)
    private HistorialMedico historialMedico;
}

