package org.example.backend_wakanda_salud.domain.usuarios.medicos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.backend_wakanda_salud.domain.centroSalud.CentroSalud;
import org.example.backend_wakanda_salud.domain.usuarios.Usuario;

@Entity
@DiscriminatorValue("MEDICO")
@Getter
@Setter
public class Medico extends Usuario {

    @Column(nullable = false)
    private String especialidad;

    @Column(nullable = false)
    private String numeroLicencia;

    @OneToOne(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    private AgendaMedica agenda;

    @ManyToOne
    @JoinColumn(name = "centro_salud_id_id")
    private CentroSalud centroSalud;
}

