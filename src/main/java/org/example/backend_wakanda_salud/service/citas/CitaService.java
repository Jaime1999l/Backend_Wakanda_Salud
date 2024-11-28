package org.example.backend_wakanda_salud.service.citas;

import org.example.backend_wakanda_salud.domain.centroSalud.citas.Cita;
import org.example.backend_wakanda_salud.domain.centroSalud.citas.CitaNormal;
import org.example.backend_wakanda_salud.domain.centroSalud.citas.CitaUrgente;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.Medico;
import org.example.backend_wakanda_salud.domain.usuarios.pacientes.Paciente;
import org.example.backend_wakanda_salud.model.centroSalud.citas.CitaNormalDTO;
import org.example.backend_wakanda_salud.model.centroSalud.citas.CitaUrgenteDTO;
import org.example.backend_wakanda_salud.repos.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CitaService {

    private final CitaNormalRepository citaNormalRepository;
    private final CitaUrgenteRepository citaUrgenteRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final NotificacionService notificacionService;

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

        CitaNormal cita = new CitaNormal();
        cita.setFechaHora(dto.getFechaHora());
        cita.setEstado("PENDIENTE");
        cita.setMotivo(dto.getMotivo());
        cita.setCreadaPorMedico(dto.getCreadaPorMedico());
        cita.setPaciente(paciente);
        cita.setMedico(medico);

        CitaNormal savedCita = citaNormalRepository.save(cita);

        notificacionService.enviarNotificacion(
                paciente.getId(), "CITA_NORMAL", "Tu cita ha sido creada con éxito.", savedCita.getId()
        );

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

        notificacionService.enviarNotificacion(
                medico.getId(), "CITA_URGENTE", "Tienes una nueva solicitud de cita urgente.", savedCita.getId()
        );

        return savedCita.getId();
    }

    @Transactional(readOnly = true)
    public Cita obtenerCitaPorId(Long citaId) {
        return citaNormalRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada."));
    }

    @Transactional
    public void eliminarCita(Long citaId) {
        Cita cita = obtenerCitaPorId(citaId);
        if (cita instanceof CitaNormal) {
            citaNormalRepository.delete((CitaNormal) cita);
        } else if (cita instanceof CitaUrgente) {
            citaUrgenteRepository.delete((CitaUrgente) cita);
        }
    }
}


