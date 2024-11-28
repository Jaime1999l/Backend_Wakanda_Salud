package org.example.backend_wakanda_salud.service.usuario;

import org.example.backend_wakanda_salud.domain.centroSalud.CentroSalud;
import org.example.backend_wakanda_salud.domain.usuarios.Usuario;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.AgendaMedica;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.Disponibilidad;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.Medico;
import org.example.backend_wakanda_salud.domain.usuarios.pacientes.Paciente;
import org.example.backend_wakanda_salud.domain.usuarios.pacientes.historialMedico.HistorialMedico;
import org.example.backend_wakanda_salud.model.usuarios.UsuarioDTO;
import org.example.backend_wakanda_salud.model.usuarios.medicos.DisponibilidadDTO;
import org.example.backend_wakanda_salud.model.usuarios.medicos.MedicoDTO;
import org.example.backend_wakanda_salud.model.usuarios.pacientes.PacienteDTO;
import org.example.backend_wakanda_salud.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Random;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private AgendaMedicaRepository agendaMedicaRepository;

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private CentroSaludRepository centroSaludRepository;

    @Autowired
    private HistorialMedicoRepository historialMedicoRepository;

    // CRUD

    @Transactional
    public UsuarioDTO get(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapToDTO(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long create(UsuarioDTO usuarioDTO) {
        // Verificar si el usuario será un médico
        if (usuarioDTO.getRoles().contains("MEDICO")) {
            // Crear la entidad específica del médico
            Medico medico = new Medico();
            mapToEntity(usuarioDTO, medico);

            // Validar y asignar especialidad
            if (usuarioDTO instanceof MedicoDTO medicoDTO) {
                if (medicoDTO.getEspecialidad() == null || medicoDTO.getEspecialidad().isEmpty()) {
                    medico.setEspecialidad(medicoService.generarEspecialidadAleatoria());
                } else {
                    medico.setEspecialidad(medicoDTO.getEspecialidad());
                }

                // Validar y asignar número de licencia
                if (medicoDTO.getNumeroLicencia() == null || medicoDTO.getNumeroLicencia().isEmpty()) {
                    medico.setNumeroLicencia(medicoService.generarNumeroLicenciaAleatorio());
                } else {
                    medico.setNumeroLicencia(medicoDTO.getNumeroLicencia());
                }
            } else {
                throw new RuntimeException("Los datos específicos de médico son obligatorios.");
            }

            // Configurar agenda médica
            if (medico.getAgenda() == null) {
                AgendaMedica agenda = new AgendaMedica();
                medico.setAgenda(agenda);

                // Generar disponibilidad aleatoria para la agenda
                generarDisponibilidadAleatoria(agenda);
            } else {
                medico.setAgenda(agendaMedicaRepository.save(medico.getAgenda()));
            }

            // Asignar un centro de salud aleatorio si no está especificado
            if (medico.getCentroSalud() == null) {
                List<CentroSalud> centrosDisponibles = centroSaludRepository.findAll();
                if (!centrosDisponibles.isEmpty()) {
                    CentroSalud centroAleatorio = centrosDisponibles.get((int) (Math.random() * centrosDisponibles.size()));
                    medico.setCentroSalud(centroAleatorio);
                } else {
                    throw new RuntimeException("No hay centros de salud disponibles para asignar.");
                }
            } else{
                medico.setCentroSalud(centroSaludRepository.save(medico.getCentroSalud()));
            }

            return medicoRepository.save(medico).getId();
        }

        // Verificar si el usuario será un paciente
        if (usuarioDTO.getRoles().contains("PACIENTE")) {
            // Crear la entidad específica del paciente
            Paciente paciente = new Paciente();
            mapToEntity(usuarioDTO, paciente);

            // Los atributos específicos de `Paciente` están presentes en `PacienteDTO`
            if (usuarioDTO instanceof PacienteDTO pacienteDTO) {
                if (pacienteDTO.getNumeroHistoriaClinica() == null || pacienteDTO.getNumeroHistoriaClinica().isEmpty()) {
                    paciente.setNumeroHistoriaClinica(generarNumeroHistoriaClinicaAleatorio());
                } else {
                    paciente.setNumeroHistoriaClinica(pacienteDTO.getNumeroHistoriaClinica());
                }
                if (pacienteDTO.getFechaNacimiento() == null) {
                    paciente.setFechaNacimiento(generarFechaNacimientoAleatoria());
                } else {
                    paciente.setFechaNacimiento(pacienteDTO.getFechaNacimiento());
                }

                if (pacienteDTO.getDireccion() == null || pacienteDTO.getDireccion().isEmpty()) {
                    paciente.setDireccion(generarDireccionAleatoria());
                } else {
                    paciente.setDireccion(pacienteDTO.getDireccion());
                }
                // Crear el historial médico
                HistorialMedico historialMedico = new HistorialMedico();
                historialMedico.setPaciente(paciente);
                historialMedico.setEntradas(new ArrayList<>());
                paciente.setHistorialMedico(historialMedico);
                historialMedicoRepository.save(historialMedico);
            } else {
                throw new RuntimeException("Los datos específicos de paciente son obligatorios.");
            }

            return pacienteRepository.save(paciente).getId();
        }

        // Si no tiene roles válidos, lanzar excepción
        throw new RuntimeException("El usuario debe tener un rol válido: MEDICO o PACIENTE");
    }


    @Transactional
    public void update(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        mapToEntity(usuarioDTO, usuario);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void delete(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario instanceof Medico) {
            Medico medico = (Medico) usuario;
            medicoRepository.delete(medico);
        } else if (usuario instanceof Paciente) {
            Paciente paciente = (Paciente) usuario;
            pacienteRepository.delete(paciente);
        } else {
            throw new RuntimeException("El usuario no tiene un tipo válido para eliminar");
        }
    }

    // Métodos de mapeo

    public UsuarioDTO mapToDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellidos(usuario.getApellidos());
        dto.setEmail(usuario.getEmail());
        dto.setCredencialesId(usuario.getCredenciales() != null ? usuario.getCredenciales().getId() : null);
        dto.setRoles(usuario.getRoles());
        return dto;
    }

    public void mapToEntity(UsuarioDTO dto, Usuario usuario) {
        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        usuario.setEmail(dto.getEmail());
    }

    // Métodos auxiliares

    @Transactional
    public void agregarDisponibilidad(Long medicoId, DisponibilidadDTO disponibilidadDTO) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        Disponibilidad disponibilidad = new Disponibilidad();

        AgendaMedica agenda = medico.getAgenda();
        if (agenda == null) {
            agenda = new AgendaMedica();
            agenda.setMedico(medico);

            // Crear la entidad Disponibilidad
            disponibilidad.setFecha(disponibilidadDTO.getFecha());
            disponibilidad.setHoraInicio(disponibilidadDTO.getHoraInicio());
            disponibilidad.setHoraFin(disponibilidadDTO.getHoraFin());
            disponibilidad.setDisponible(disponibilidadDTO.getDisponible());
            disponibilidad.setAgendaMedica(agenda);

        }
        agendaMedicaRepository.save(agenda);
    }

    @Transactional
    public void generarDisponibilidadAleatoria(AgendaMedica agenda) {
        Disponibilidad disponibilidad = new Disponibilidad();

        // Obtener fecha actual
        Date fecha = new Date();

        // Horarios predefinidos (pueden ajustarse según tus necesidades)
        Time horaInicioBase = Time.valueOf("08:00:00");
        Time horaFinBase = Time.valueOf("18:00:00");

        // Generar una franja aleatoria que no esté ocupada
        Time[] horarioDisponible = obtenerHorarioDisponible(agenda, fecha, horaInicioBase, horaFinBase);
        if (horarioDisponible == null) {
            throw new RuntimeException("No hay horarios disponibles para generar una disponibilidad.");
        }

        // Configurar la disponibilidad con el horario encontrado
        disponibilidad.setFecha(fecha);
        disponibilidad.setHoraInicio(horarioDisponible[0]);
        disponibilidad.setHoraFin(horarioDisponible[1]);
        disponibilidad.setDisponible(true);
        disponibilidad.setAgendaMedica(agenda);

        // Asociar la disponibilidad a la agenda
        if (agenda.getHorariosDisponibles() == null) {
            agenda.setHorariosDisponibles(new ArrayList<>());
        }
        agenda.getHorariosDisponibles().add(disponibilidad);

        // Mapear la entidad a DTO
        DisponibilidadDTO dto = new DisponibilidadDTO();
        dto.setId(disponibilidad.getId());
        dto.setFecha(disponibilidad.getFecha());
        dto.setHoraInicio(disponibilidad.getHoraInicio());
        dto.setHoraFin(disponibilidad.getHoraFin());
        dto.setDisponible(disponibilidad.getDisponible());
        dto.setAgendaMedicaId(agenda.getId());

        agregarDisponibilidad(agenda.getMedico().getId(), dto);

    }

    private Time[] obtenerHorarioDisponible(AgendaMedica agenda, Date fecha, Time horaInicioBase, Time horaFinBase) {
        List<Disponibilidad> horariosOcupados = agenda.getHorariosDisponibles().stream()
                .filter(d -> d.getFecha().equals(fecha))
                .toList();

        // Crear lista de intervalos libres
        List<Time[]> horariosLibres = new ArrayList<>();

        // Buscar huecos entre los horarios ocupados
        Time cursor = horaInicioBase;
        for (Disponibilidad d : horariosOcupados) {
            if (cursor.before(d.getHoraInicio())) {
                horariosLibres.add(new Time[]{cursor, d.getHoraInicio()});
            }
            cursor = d.getHoraFin().after(cursor) ? d.getHoraFin() : cursor;
        }

        // Agregar el último hueco hasta el final del horario base
        if (cursor.before(horaFinBase)) {
            horariosLibres.add(new Time[]{cursor, horaFinBase});
        }

        // Seleccionar aleatoriamente un intervalo disponible
        if (!horariosLibres.isEmpty()) {
            Time[] intervaloSeleccionado = horariosLibres.get(new Random().nextInt(horariosLibres.size()));
            // Ajustar el horario generado a un rango fijo de 2 horas
            Time horaInicio = intervaloSeleccionado[0];
            Time horaFin = Time.valueOf(
                    intervaloSeleccionado[0].toLocalTime().plusHours(2).isBefore(intervaloSeleccionado[1].toLocalTime())
                            ? intervaloSeleccionado[0].toLocalTime().plusHours(2)
                            : intervaloSeleccionado[1].toLocalTime()
            );
            return new Time[]{horaInicio, horaFin};
        }

        return null; // No hay horarios disponibles
    }

    public String generarDireccionAleatoria() {
        String[] calles = {
                "Calle Mayor", "Gran Vía", "Paseo de la Castellana", "Calle Alcalá",
                "Calle Serrano", "Calle Princesa", "Calle Atocha", "Calle Goya"
        };

        int numero = (int) (Math.random() * 200) + 1; // Números entre 1 y 200
        String calleAleatoria = calles[(int) (Math.random() * calles.length)];
        return calleAleatoria + " " + numero + ", Madrid";
    }

    public Date generarFechaNacimientoAleatoria() {
        int edadMinima = 18; // Edad mínima
        int edadMaxima = 90; // Edad máxima

        LocalDate hoy = LocalDate.now();
        LocalDate fechaInicio = hoy.minus(Period.ofYears(edadMaxima));
        LocalDate fechaFin = hoy.minus(Period.ofYears(edadMinima));

        long start = fechaInicio.toEpochDay();
        long end = fechaFin.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(start, end + 1);

        LocalDate randomDate = LocalDate.ofEpochDay(randomDay);
        return Date.from(randomDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public String generarNumeroHistoriaClinicaAleatorio() {
        int longitud = 10; // Longitud del número de historia clínica
        StringBuilder numeroHistoria = new StringBuilder();

        for (int i = 0; i < longitud; i++) {
            int digito = (int) (Math.random() * 10); // Dígitos entre 0 y 9
            numeroHistoria.append(digito);
        }

        return numeroHistoria.toString();
    }
}
