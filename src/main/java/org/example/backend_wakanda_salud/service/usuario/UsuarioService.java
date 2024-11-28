package org.example.backend_wakanda_salud.service.usuario;

import org.example.backend_wakanda_salud.domain.centroSalud.CentroSalud;
import org.example.backend_wakanda_salud.domain.centroSalud.citas.CitaNormal;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.backend_wakanda_salud.domain.centroSalud.citas.Cita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.*;
import java.util.Random;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private CitaNormalRepository citaNormalRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private AgendaMedicaRepository agendaMedicaRepository;

    @Autowired
    private CentroSaludRepository centroSaludRepository;

    @Autowired
    private HistorialMedicoRepository historialMedicoRepository;

    private final MedicoService medicoService;

    public UsuarioService(@Lazy MedicoService medicoService) {
        this.medicoService = medicoService;
    }

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
            Medico medico = new Medico();
            mapToEntity(usuarioDTO, medico);

            if (usuarioDTO instanceof MedicoDTO medicoDTO) {
                if (medicoDTO.getEspecialidad() == null || medicoDTO.getEspecialidad().isEmpty()) {
                    medico.setEspecialidad(medicoService.generarEspecialidadAleatoria());
                } else {
                    medico.setEspecialidad(medicoDTO.getEspecialidad());
                }

                if (medicoDTO.getNumeroLicencia() == null || medicoDTO.getNumeroLicencia().isEmpty()) {
                    medico.setNumeroLicencia(medicoService.generarNumeroLicenciaAleatorio());
                } else {
                    medico.setNumeroLicencia(medicoDTO.getNumeroLicencia());
                }
            } else {
                throw new RuntimeException("Los datos específicos de médico son obligatorios.");
            }

            if (medico.getAgenda() == null) {
                AgendaMedica agenda = new AgendaMedica();
                medico.setAgenda(agenda);
                agenda.setMedico(medico);

                // Persistir el médico y la agenda antes de generar disponibilidades
                medicoRepository.save(medico);
                agendaMedicaRepository.save(agenda);

                // Generar disponibilidad aleatoria
                generarDisponibilidadAleatoria(agenda);
            } else {
                medico.setAgenda(agendaMedicaRepository.save(medico.getAgenda()));
            }

            if (medico.getCentroSalud() == null) {
                List<CentroSalud> centrosDisponibles = centroSaludRepository.findAll();
                if (!centrosDisponibles.isEmpty()) {
                    CentroSalud centroAleatorio = centrosDisponibles.get((int) (Math.random() * centrosDisponibles.size()));
                    medico.setCentroSalud(centroAleatorio);
                } else {
                    throw new RuntimeException("No hay centros de salud disponibles para asignar.");
                }
            } else {
                medico.setCentroSalud(centroSaludRepository.save(medico.getCentroSalud()));
            }

            return medicoRepository.save(medico).getId();
        }

        // Verificar si el usuario será un paciente
        if (usuarioDTO.getRoles().contains("PACIENTE")) {
            Paciente paciente = new Paciente();
            mapToEntity(usuarioDTO, paciente);

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

                // Guardar el paciente antes de asociar el historial médico
                pacienteRepository.save(paciente);

                // Crear el historial médico y asociarlo al paciente
                HistorialMedico historialMedico = new HistorialMedico();
                historialMedico.setPaciente(paciente);
                historialMedico.setEntradas(new ArrayList<>());
                paciente.setHistorialMedico(historialMedico);

                historialMedicoRepository.save(historialMedico);
            } else {
                throw new RuntimeException("Los datos específicos de paciente son obligatorios.");
            }

            return paciente.getId();
        }

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

    // Generar disponibilidad en 3 franjas de 8 horas
    @Transactional
    public void generarDisponibilidadAleatoria(AgendaMedica agenda) {
        if (agenda.getId() == null) {
            throw new RuntimeException("La AgendaMedica debe estar guardada antes de generar disponibilidades.");
        }

        // Crear 3 bloques de 8 horas
        // Bloque 1: 00:00 - 08:00
        Time horaInicio1 = Time.valueOf("00:00:00");
        Time horaFin1 = Time.valueOf("08:00:00");

        // Bloque 2: 08:00 - 16:00
        Time horaInicio2 = Time.valueOf("08:00:00");
        Time horaFin2 = Time.valueOf("16:00:00");

        // Bloque 3: 16:00 - 23:59
        Time horaInicio3 = Time.valueOf("16:00:00");
        Time horaFin3 = Time.valueOf("23:59:59");

        // Crear y guardar las disponibilidades para cada bloque de 8 horas
        crearDisponibilidad(agenda, horaInicio1, horaFin1);
        crearDisponibilidad(agenda, horaInicio2, horaFin2);
        crearDisponibilidad(agenda, horaInicio3, horaFin3);
    }

    // Función para crear disponibilidades
    private void crearDisponibilidad(AgendaMedica agenda, Time horaInicio, Time horaFin) {
        Disponibilidad disponibilidad = new Disponibilidad();
        disponibilidad.setFecha(new Date()); // Fecha actual
        disponibilidad.setHoraInicio(horaInicio);
        disponibilidad.setHoraFin(horaFin);
        disponibilidad.setDisponible(true);
        disponibilidad.setAgendaMedica(agenda);

        // Guardar la disponibilidad directamente en la base de datos
        disponibilidadRepository.save(disponibilidad);
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
