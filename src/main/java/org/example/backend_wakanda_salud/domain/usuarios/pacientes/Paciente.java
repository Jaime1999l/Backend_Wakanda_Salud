package org.example.backend_wakanda_salud.domain.usuarios.pacientes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.backend_wakanda_salud.domain.usuarios.Usuario;
import org.example.backend_wakanda_salud.domain.usuarios.pacientes.historialMedico.HistorialMedico;

import java.util.Date;

@Entity
@DiscriminatorValue("PACIENTE")
@Getter
@Setter
public class Paciente extends Usuario {

    @Column(nullable = false)
    private String numeroHistoriaClinica;

    @Column(nullable = false)
    private Date fechaNacimiento;

    @Column(nullable = false)
    private String direccion;

    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private HistorialMedico historialMedico;
}

