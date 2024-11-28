package org.example.backend_wakanda_salud.service.citas;

import org.example.backend_wakanda_salud.domain.centroSalud.CentroSalud;
import org.example.backend_wakanda_salud.domain.centroSalud.SistemaCitas;
import org.example.backend_wakanda_salud.domain.centroSalud.citas.Cita;
import org.example.backend_wakanda_salud.domain.centroSalud.citas.CitaNormal;
import org.example.backend_wakanda_salud.domain.centroSalud.citas.CitaUrgente;
import org.example.backend_wakanda_salud.model.centroSalud.citas.CitaNormalDTO;
import org.example.backend_wakanda_salud.model.centroSalud.citas.CitaUrgenteDTO;
import org.example.backend_wakanda_salud.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SistemaCitasService {

    @Autowired
    private CentroSaludRepository centroSaludRepository;

    @Autowired
    private CitaService citaService;

    private final SistemaCitasRepository sistemaCitasRepository;
    private final CitaNormalRepository citaNormalRepository;
    private final CitaUrgenteRepository citaUrgenteRepository;
    private final NotificacionService notificacionService;

    public SistemaCitasService(SistemaCitasRepository sistemaCitasRepository,
                               CitaNormalRepository citaNormalRepository,
                               CitaUrgenteRepository citaUrgenteRepository,
                               NotificacionService notificacionService) {
        this.sistemaCitasRepository = sistemaCitasRepository;
        this.citaNormalRepository = citaNormalRepository;
        this.citaUrgenteRepository = citaUrgenteRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public Long crearSistemaCitas(String descripcion, Long centroSaludId) {
        CentroSalud centroSalud = centroSaludRepository.findById(centroSaludId)
                .orElseThrow(() -> new RuntimeException("Centro de salud no encontrado."));

        SistemaCitas sistema = new SistemaCitas();
        sistema.setDescripcion(descripcion);
        sistema.setCentroSalud(centroSalud);
        sistema.setCitas(List.of());

        return sistemaCitasRepository.save(sistema).getId();
    }

    @Transactional(readOnly = true)
    public List<Cita> obtenerCitasDeSistema(Long sistemaId) {
        SistemaCitas sistema = sistemaCitasRepository.findById(sistemaId)
                .orElseThrow(() -> new RuntimeException("Sistema de citas no encontrado."));
        return sistema.getCitas();
    }

    @Transactional
    public Long agregarCitaNormal(Long sistemaId, CitaNormalDTO citaNormalDTO) {
        SistemaCitas sistema = sistemaCitasRepository.findById(sistemaId)
                .orElseThrow(() -> new RuntimeException("Sistema de citas no encontrado."));

        Long citaId = citaService.crearCitaNormal(citaNormalDTO);
        CitaNormal cita = citaNormalRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Error al asociar la cita normal."));

        sistema.getCitas().add(cita);
        sistemaCitasRepository.save(sistema);

        notificarCitaCreada(cita, "CITA_NORMAL");

        return citaId;
    }

    @Transactional
    public Long agregarCitaUrgente(Long sistemaId, CitaUrgenteDTO citaUrgenteDTO) {
        SistemaCitas sistema = sistemaCitasRepository.findById(sistemaId)
                .orElseThrow(() -> new RuntimeException("Sistema de citas no encontrado."));

        Long citaId = citaService.crearCitaUrgente(citaUrgenteDTO);
        CitaUrgente cita = citaUrgenteRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Error al asociar la cita urgente."));

        sistema.getCitas().add(cita);
        sistemaCitasRepository.save(sistema);

        notificarCitaCreada(cita, "CITA_URGENTE");

        return citaId;
    }

    @Transactional
    public void eliminarCitaDeSistema(Long sistemaId, Long citaId) {
        SistemaCitas sistema = sistemaCitasRepository.findById(sistemaId)
                .orElseThrow(() -> new RuntimeException("Sistema de citas no encontrado."));

        Cita cita = citaService.obtenerCitaPorId(citaId);
        sistema.getCitas().remove(cita);
        citaService.eliminarCita(citaId);

        notificacionService.enviarNotificacion(
                cita.getPaciente().getId(),
                "CITA_ELIMINADA",
                "Tu cita ha sido eliminada.",
                citaId
        );

        if (cita instanceof CitaNormal) {
            notificacionService.enviarNotificacion(
                    cita.getMedico().getId(),
                    "CITA_ELIMINADA",
                    "Una cita normal asignada ha sido eliminada.",
                    citaId
            );
        } else if (cita instanceof CitaUrgente) {
            notificacionService.enviarNotificacion(
                    cita.getMedico().getId(),
                    "CITA_ELIMINADA",
                    "Una cita urgente asignada ha sido eliminada.",
                    citaId
            );
        }
    }

    // Función auxiliar para notificar creación de citas
    private void notificarCitaCreada(Cita cita, String tipoCita) {
        notificacionService.enviarNotificacion(
                cita.getPaciente().getId(),
                tipoCita,
                "Tu " + (tipoCita.equals("CITA_NORMAL") ? "cita normal" : "cita urgente") + " ha sido creada exitosamente.",
                cita.getId()
        );

        notificacionService.enviarNotificacion(
                cita.getMedico().getId(),
                tipoCita,
                "Tienes una nueva " + (tipoCita.equals("CITA_NORMAL") ? "cita normal" : "cita urgente") + " asignada.",
                cita.getId()
        );
    }
}
