package org.example.backend_wakanda_salud.service.citas;

import org.example.backend_wakanda_salud.domain.centroSalud.citas.Cita;
import org.example.backend_wakanda_salud.domain.centroSalud.citas.CitaNormal;
import org.example.backend_wakanda_salud.domain.centroSalud.citas.CitaUrgente;
import org.example.backend_wakanda_salud.domain.centroSalud.citas.Notificacion;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.Medico;
import org.example.backend_wakanda_salud.domain.usuarios.pacientes.Paciente;
import org.example.backend_wakanda_salud.model.centroSalud.citas.CitaNormalDTO;
import org.example.backend_wakanda_salud.model.centroSalud.citas.CitaUrgenteDTO;
import org.example.backend_wakanda_salud.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitaService {

    private final CitaNormalRepository citaNormalRepository;
    private final CitaUrgenteRepository citaUrgenteRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final NotificacionService notificacionService;

    @Autowired
    private NotificacionRepository notificacionRepository;

    public CitaService(CitaNormalRepository citaNormalRepository,
                       CitaUrgenteRepository citaUrgenteRepository,
                       PacienteRepository pacienteRepository,
                       MedicoRepository medicoRepository,
                       NotificacionService notificacionService) {
        this.citaNormalRepository = citaNormalRepository;
        this.citaUrgenteRepository = citaUrgenteRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public Long crearCitaNormal(CitaNormalDTO dto) {
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado."));
        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado."));

        if (!verificarDisponibilidadMedico(medico, dto.getFechaHora())) {
            throw new RuntimeException("El médico no está disponible en la fecha y hora seleccionadas.");
        }

        CitaNormal cita = new CitaNormal();
        cita.setFechaHora(dto.getFechaHora());
        cita.setEstado("PENDIENTE");
        cita.setMotivo(dto.getMotivo());
        cita.setCreadaPorMedico(dto.getCreadaPorMedico());
        cita.setPaciente(paciente);
        cita.setMedico(medico);

        CitaNormal savedCita = citaNormalRepository.save(cita);

        // Enviar notificaciones
        notificacionService.enviarNotificacion(
                paciente.getId(), "CITA_NORMAL", "Tu cita normal ha sido creada exitosamente.", savedCita.getId());
        notificacionService.enviarNotificacion(
                medico.getId(), "CITA_NORMAL", "Tienes una nueva cita normal asignada.", savedCita.getId());

        return savedCita.getId();
    }

    @Transactional
    public Long crearCitaUrgente(CitaUrgenteDTO dto) {
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado."));
        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado."));

        CitaUrgente cita = new CitaUrgente();
        cita.setFechaHora(dto.getFechaHora());
        cita.setEstado("PROPUESTA");
        cita.setMotivoUrgencia(dto.getMotivoUrgencia());
        cita.setNivelPrioridad(dto.getNivelPrioridad());
        cita.setPaciente(paciente);
        cita.setMedico(medico);

        CitaUrgente savedCita = citaUrgenteRepository.save(cita);

        // Enviar notificación al médico
        notificacionService.enviarNotificacion(
                medico.getId(), "CITA_URGENTE", "Tienes una nueva solicitud de cita urgente.", savedCita.getId());

        return savedCita.getId();
    }

    @Transactional(readOnly = true)
    public CitaNormal obtenerCitaNormalPorId(Long citaId) {
        return citaNormalRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita normal no encontrada."));
    }

    @Transactional(readOnly = true)
    public CitaUrgente obtenerCitaUrgentePorId(Long citaId) {
        return citaUrgenteRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita urgente no encontrada."));
    }

    @Transactional(readOnly = true)
    public Cita obtenerCitaPorId(Long citaId) {
        if (citaNormalRepository.existsById(citaId)) {
            return citaNormalRepository.findById(citaId)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada."));
        } else if (citaUrgenteRepository.existsById(citaId)) {
            return citaUrgenteRepository.findById(citaId)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada."));
        } else {
            throw new RuntimeException("Cita no encontrada.");
        }
    }

    @Transactional
    public void eliminarCita(Long citaId) {
        Cita cita = obtenerCitaPorId(citaId);

        // Marcar notificaciones asociadas como leídas
        List<Notificacion> notificacionesAsociadas = notificacionRepository.findAll().stream()
                .filter(notificacion -> notificacion.getReferenciaCitaId().equals(citaId))
                .collect(Collectors.toList());

        for (Notificacion notificacion : notificacionesAsociadas) {
            if (!notificacion.getLeida()) {
                notificacion.setLeida(true);
            }
        }
        notificacionRepository.saveAll(notificacionesAsociadas);

        // Eliminar la cita
        if (cita instanceof CitaNormal) {
            citaNormalRepository.delete((CitaNormal) cita);
        } else if (cita instanceof CitaUrgente) {
            citaUrgenteRepository.delete((CitaUrgente) cita);
        }

        // Enviar notificación al paciente
        notificacionService.enviarNotificacion(
                cita.getPaciente().getId(),
                "CITA_ELIMINADA",
                "Tu cita ha sido eliminada.",
                citaId
        );
    }

    // Función auxiliar para verificar la disponibilidad del médico
    private boolean verificarDisponibilidadMedico(Medico medico, Date fechaHora) {
        if (medico.getAgenda() == null || medico.getAgenda().getHorariosDisponibles().isEmpty()) {
            return false; // No hay agenda médica o no tiene horarios configurados
        }

        return medico.getAgenda().getHorariosDisponibles().stream()
                .anyMatch(disponibilidad -> disponibilidad.getDisponible()
                        && disponibilidad.getFecha().equals(fechaHora));
    }

    @Transactional
    public void aceptarCitaUrgente(Long citaId) {
        CitaUrgente cita = citaUrgenteRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita urgente no encontrada."));

        cita.setEstado("ACEPTADA");
        citaUrgenteRepository.save(cita);

        // Marcar notificaciones asociadas como leídas
        List<Notificacion> notificaciones = notificacionRepository.findAll().stream()
                .filter(notificacion -> notificacion.getReferenciaCitaId().equals(citaId)
                        && "CITA_URGENTE".equals(notificacion.getTipo()))
                .collect(Collectors.toList());

        for (Notificacion notificacion : notificaciones) {
            notificacion.setLeida(true);
        }
        notificacionRepository.saveAll(notificaciones);

        // Enviar nueva notificación al paciente
        notificacionService.enviarNotificacion(
                cita.getPaciente().getId(),
                "CITA_URGENTE",
                "Tu cita urgente ha sido aceptada.",
                cita.getId()
        );
    }
}
