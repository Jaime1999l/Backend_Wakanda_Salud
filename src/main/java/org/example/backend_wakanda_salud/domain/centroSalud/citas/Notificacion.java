package org.example.backend_wakanda_salud.domain.centroSalud.citas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.backend_wakanda_salud.domain.usuarios.Usuario;

import java.util.Date;

@Entity
@Table(name = "Notificaciones")
@Getter
@Setter
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String tipo; // 'CITA_NORMAL', 'CITA_URGENTE'

    @Column(nullable = false)
    private String mensaje;

    @Column(nullable = false)
    private Date fechaHoraEnvio;

    @Column(nullable = false)
    private Boolean leida;

    @Column(nullable = false)
    private Long referenciaCitaId; // ID de la cita asociada
}
