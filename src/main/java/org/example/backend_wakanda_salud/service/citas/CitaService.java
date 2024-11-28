package org.example.backend_wakanda_salud.service.citas;

import org.example.backend_wakanda_salud.domain.centroSalud.SistemaCitas;
import org.example.backend_wakanda_salud.domain.centroSalud.citas.*;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.AgendaMedica;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.Disponibilidad;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.Medico;
import org.example.backend_wakanda_salud.domain.usuarios.pacientes.Paciente;
import org.example.backend_wakanda_salud.model.centroSalud.citas.CitaNormalDTO;
import org.example.backend_wakanda_salud.model.centroSalud.citas.CitaUrgenteDTO;
import org.example.backend_wakanda_salud.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitaService {

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    private final CitaNormalRepository citaNormalRepository;
    private final CitaUrgenteRepository citaUrgenteRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final SistemaCitasRepository sistemaCitasRepository;
    private final NotificacionRepository notificacionRepository;
    private final NotificacionService notificacionService;

    @Autowired
    public CitaService(CitaNormalRepository citaNormalRepository,
                       CitaUrgenteRepository citaUrgenteRepository,
                       PacienteRepository pacienteRepository,
                       MedicoRepository medicoRepository,
                       SistemaCitasRepository sistemaCitasRepository,
                       NotificacionRepository notificacionRepository,
                       NotificacionService notificacionService) {
        this.citaNormalRepository = citaNormalRepository;
        this.citaUrgenteRepository = citaUrgenteRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
        this.sistemaCitasRepository = sistemaCitasRepository;
        this.notificacionRepository = notificacionRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public Long crearCitaNormal(CitaNormalDTO dto) {
        // Obtener el paciente y el médico desde las bases de datos
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado."));
        Medico medico = medicoRepository.findById(dto.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado."));

        // Comprobar si la fechaHora es null
        Date fechaHora = dto.getFechaHora();
        if (fechaHora == null) {
            throw new RuntimeException("La fecha y hora solicitadas no pueden ser nulas.");
        }

        // Verificar la disponibilidad del médico en las tres franjas de 8 horas
        Disponibilidad disponibilidad = verificarDisponibilidadEnFranja(medico, fechaHora, "00:00", "08:00");
        if (disponibilidad == null) {
            disponibilidad = verificarDisponibilidadEnFranja(medico, fechaHora, "08:00", "16:00");
        }
        if (disponibilidad == null) {
            disponibilidad = verificarDisponibilidadEnFranja(medico, fechaHora, "16:00", "23:59");
        }

        // Si no hay disponibilidad en ninguna franja, lanzamos un error
        if (disponibilidad == null || !disponibilidad.getDisponible()) {
            List<Disponibilidad> horariosAlternativos = sugerirHorariosDisponibles(medico, fechaHora);

            String mensaje = "El médico no está disponible en la fecha y hora seleccionadas.";
            if (!horariosAlternativos.isEmpty()) {
                mensaje += " Horarios alternativos disponibles: " + horariosAlternativos.stream()
                        .map(d -> d.getHoraInicio() + " - " + d.getHoraFin())
                        .collect(Collectors.joining(", "));
            } else {
                mensaje += " No hay horarios alternativos disponibles.";
            }
            throw new RuntimeException(mensaje);  // Excepción si no se encuentra disponibilidad
        }

        // Crear la cita normal
        CitaNormal cita = new CitaNormal();
        cita.setFechaHora(dto.getFechaHora());
        cita.setEstado("PENDIENTE");
        cita.setMotivo(dto.getMotivo());  // Asignar el motivo solo para CitaNormal
        cita.setCreadaPorMedico(true);  // Aseguramos que se marca como creada por el médico
        cita.setPaciente(paciente);
        cita.setMedico(medico);

        // Asignar la cita al sistema de citas
        SistemaCitas sistemaCitas = sistemaCitasRepository.findByCentroSalud_Id(medico.getCentroSalud().getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sistema de citas no encontrado para el centro de salud."));
        cita.setSistemaCitas(sistemaCitas);

        // Actualizar la disponibilidad
        disponibilidad.setDisponible(false);
        disponibilidadRepository.save(disponibilidad);

        // Guardar la cita en la base de datos
        CitaNormal savedCita = citaNormalRepository.save(cita);

        // Asociar la cita al sistema de citas
        sistemaCitas.getCitas().add(savedCita);
        sistemaCitasRepository.save(sistemaCitas);

        // Enviar notificaciones a paciente y médico
        enviarNotificacionesCitaNormal(savedCita, paciente, medico);

        return savedCita.getId();  // Retornar el ID de la cita creada
    }

    private Disponibilidad verificarDisponibilidadEnFranja(Medico medico, Date fechaHora, String horaInicio, String horaFin) {
        if (medico == null || medico.getAgenda() == null) {
            throw new RuntimeException("El médico o su agenda no están disponibles.");
        }

        // Convertimos la fechaHora y las franjas a LocalDateTime para una comparación más robusta
        LocalDateTime fechaHoraLocal = new java.sql.Timestamp(fechaHora.getTime()).toLocalDateTime();

        // Convertimos las horas de inicio y fin a LocalTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime inicioFranja = LocalTime.parse(horaInicio, formatter);
        LocalTime finFranja = LocalTime.parse(horaFin, formatter);

        // Verificar disponibilidad de la franja comparando fecha, hora, mes y año
        return medico.getAgenda().getHorariosDisponibles().stream()
                .filter(d -> {
                    // Asegurarse de que la fecha no sea null
                    if (d.getFecha() == null || fechaHora == null) {
                        return false;
                    }

                    // Convertir la fecha de disponibilidad a LocalDateTime
                    LocalDateTime disponibilidadFecha = new java.sql.Timestamp(d.getFecha().getTime()).toLocalDateTime();
                    LocalTime horaInicioDisponibilidad = d.getHoraInicio().toLocalTime();
                    LocalTime horaFinDisponibilidad = d.getHoraFin().toLocalTime();

                    // Compara solo el día, mes y año (sin la hora) de la fecha
                    boolean mismaFecha = disponibilidadFecha.toLocalDate().equals(fechaHoraLocal.toLocalDate());

                    // Compara si la franja está dentro del rango
                    boolean dentroFranja = !horaInicioDisponibilidad.isBefore(inicioFranja) && !horaFinDisponibilidad.isAfter(finFranja);
                    boolean disponible = d.getDisponible();

                    return mismaFecha && dentroFranja && disponible;
                })
                .findFirst()
                .orElse(null);
    }

    private void enviarNotificacionesCitaNormal(CitaNormal cita, Paciente paciente, Medico medico) {
        // Notificación al paciente
        notificacionService.enviarNotificacion(
                paciente.getId(),    // ID del paciente
                "CITA_NORMAL",       // Tipo de notificación
                "Tu cita normal ha sido creada exitosamente.", // Mensaje de notificación
                cita.getId()         // ID de la cita
        );

        // Notificación al médico
        notificacionService.enviarNotificacion(
                medico.getId(),      // ID del médico
                "CITA_NORMAL",       // Tipo de notificación
                "Tienes una nueva cita normal asignada.", // Mensaje de notificación
                cita.getId()         // ID de la cita
        );
    }

    private List<Disponibilidad> sugerirHorariosDisponibles(Medico medico, Date fecha) {
        return medico.getAgenda().getHorariosDisponibles().stream()
                .filter(d -> d.getFecha().equals(fecha) && d.getDisponible())
                .collect(Collectors.toList());
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
    public CitaNormalDTO obtenerCitaNormalPorId(Long citaId) {
        CitaNormal citaNormal = citaNormalRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita normal no encontrada."));
        return mapToDTO(citaNormal);
    }

    @Transactional(readOnly = true)
    public CitaUrgenteDTO obtenerCitaUrgentePorId(Long citaId) {
        CitaUrgente citaUrgente = citaUrgenteRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita urgente no encontrada."));
        return mapToDTO(citaUrgente);
    }


    @Transactional(readOnly = true)
    public Cita obtenerCitaPorId(Long citaId) {
        if (citaNormalRepository.existsById(citaId)) {
            return citaNormalRepository.findById(citaId)
                    .orElseThrow(() -> new RuntimeException("Cita normal no encontrada."));
        } else if (citaUrgenteRepository.existsById(citaId)) {
            return citaUrgenteRepository.findById(citaId)
                    .orElseThrow(() -> new RuntimeException("Cita urgente no encontrada."));
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

    // Metodo para mapear CitaNormal a CitaNormalDTO
    private CitaNormalDTO mapToDTO(CitaNormal citaNormal) {
        CitaNormalDTO dto = new CitaNormalDTO();
        dto.setId(citaNormal.getId());
        dto.setFechaHora(citaNormal.getFechaHora());
        dto.setPacienteId(citaNormal.getPaciente().getId());
        dto.setMedicoId(citaNormal.getMedico().getId());
        dto.setMotivo(citaNormal.getMotivo());
        return dto;
    }

    // Metodo para mapear CitaUrgente a CitaUrgenteDTO
    private CitaUrgenteDTO mapToDTO(CitaUrgente citaUrgente) {
        CitaUrgenteDTO dto = new CitaUrgenteDTO();
        dto.setId(citaUrgente.getId());
        dto.setFechaHora(citaUrgente.getFechaHora());
        dto.setPacienteId(citaUrgente.getPaciente().getId());
        dto.setMedicoId(citaUrgente.getMedico().getId());
        dto.setMotivoUrgencia(citaUrgente.getMotivoUrgencia());
        dto.setNivelPrioridad(citaUrgente.getNivelPrioridad());
        return dto;
    }

}
