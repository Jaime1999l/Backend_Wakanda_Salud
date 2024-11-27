package org.example.backend_wakanda_salud.domain.centroSalud;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.backend_wakanda_salud.domain.centroSalud.citas.Cita;

import java.util.List;

@Entity
@Table(name = "SistemaCitas")
@Getter
@Setter
public class SistemaCitas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "sistemaCitas", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_salud_id", nullable = false)
    private CentroSalud centroSalud;

    @Column(nullable = false)
    private String descripcion;
}

