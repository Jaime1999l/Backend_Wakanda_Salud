package org.example.backend_wakanda_salud.domain.centroSalud;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.Medico;
import org.example.backend_wakanda_salud.domain.usuarios.pacientes.Paciente;

import java.util.List;

@Entity
@Table(name = "CentrosSalud")
@Getter
@Setter
public class CentroSalud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String telefono;

    @OneToMany(mappedBy = "centroSalud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medico> medicos;

    @OneToMany(mappedBy = "centroSalud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paciente> pacientes;

    @OneToMany(mappedBy = "centroSalud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SistemaCitas> sistemasCitas;
}

