package org.example.backend_wakanda_salud.service.usuario;

import org.example.backend_wakanda_salud.domain.usuarios.pacientes.Paciente;
import org.example.backend_wakanda_salud.domain.usuarios.pacientes.historialMedico.HistorialMedico;
import org.example.backend_wakanda_salud.model.usuarios.pacientes.PacienteDTO;
import org.example.backend_wakanda_salud.repos.HistorialMedicoRepository;
import org.example.backend_wakanda_salud.repos.PacienteRepository;
import org.example.backend_wakanda_salud.service.usuario.pacienteHistorial.HistorialMedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private HistorialMedicoService historialMedicoService;
    @Autowired
    private HistorialMedicoRepository historialMedicoRepository;

    // CRUD
    @Transactional
    public Long create(PacienteDTO pacienteDTO) {
        // Verificar si el usuario será un paciente
        if (pacienteDTO.getRoles().contains("PACIENTE")) {
            // Crear la entidad específica del paciente
            Paciente paciente = new Paciente();
            pacienteRepository.save(paciente);

            mapToEntity(pacienteDTO, paciente);

            if (pacienteDTO.getNumeroHistoriaClinica() != null) {
                paciente.setNumeroHistoriaClinica(pacienteDTO.getNumeroHistoriaClinica());
            } else {
                paciente.setNumeroHistoriaClinica("N/A");
            }

            if (pacienteDTO.getFechaNacimiento() != null) {
                paciente.setFechaNacimiento(pacienteDTO.getFechaNacimiento());
            } else {
                paciente.setFechaNacimiento(generarFechaNacimientoAleatoria());
            }

            if (pacienteDTO.getDireccion() != null) {
                paciente.setDireccion(pacienteDTO.getDireccion());
            } else {
                paciente.setDireccion("N/A");
            }

            if (pacienteDTO.getHistorialMedicoId() != null) {
                HistorialMedico historialMedico = historialMedicoRepository.findById(pacienteDTO.getHistorialMedicoId())
                        .orElseThrow(() -> new RuntimeException("Historial médico no encontrado"));
                paciente.setHistorialMedico(historialMedico);
            } else {
                historialMedicoService.crearHistorial(paciente.getId());
                paciente.setHistorialMedico(paciente.getHistorialMedico());
                update(paciente.getId(), pacienteDTO);
            }

            return paciente.getId();

        } else {
            throw new RuntimeException("Los datos específicos de paciente son obligatorios.");
        }
    }

    private Date generarFechaNacimientoAleatoria() {
        Calendar calendar = Calendar.getInstance();

        // Calcular fechas de inicio y fin para el rango (por ejemplo, 18 a 90 años atrás)
        calendar.add(Calendar.YEAR, -90); // Fecha de inicio (90 años atrás)
        Date fechaInicio = calendar.getTime();

        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18); // Fecha de fin (18 años atrás)
        Date fechaFin = calendar.getTime();

        // Generar fecha aleatoria dentro del rango
        long fechaAleatoriaMillis = ThreadLocalRandom.current().nextLong(fechaInicio.getTime(), fechaFin.getTime());
        return new Date(fechaAleatoriaMillis);
    }

    @Transactional(readOnly = true)
    public PacienteDTO get(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        return mapToDTO(paciente);
    }

    @Transactional(readOnly = true)
    public List<PacienteDTO> findAll() {
        return pacienteRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(Long id, PacienteDTO pacienteDTO) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        mapToEntity(pacienteDTO, paciente);
        pacienteRepository.save(paciente);
    }

    @Transactional
    public void delete(Long id) {
        pacienteRepository.deleteById(id);
    }

    // Lógica de negocio

    @Transactional
    public void visualizarHistorial(Long pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        System.out.println("Historial médico del paciente: " + paciente.getHistorialMedico());
    }

    // Métodos de mapeo

    public PacienteDTO mapToDTO(Paciente paciente) {
        PacienteDTO dto = new PacienteDTO();
        dto.setId(paciente.getId());
        dto.setNumeroHistoriaClinica(paciente.getNumeroHistoriaClinica());
        dto.setFechaNacimiento(paciente.getFechaNacimiento());
        dto.setDireccion(paciente.getDireccion());
        dto.setHistorialMedicoId(paciente.getHistorialMedico() != null ? paciente.getHistorialMedico().getId() : null);
        return dto;
    }

    public Paciente mapToEntity(PacienteDTO dto, Paciente paciente) {
        paciente.setNumeroHistoriaClinica(dto.getNumeroHistoriaClinica());
        paciente.setFechaNacimiento(dto.getFechaNacimiento());
        paciente.setDireccion(dto.getDireccion());

        // Crear historial médico si no existe
        if (paciente.getHistorialMedico() == null) {
            historialMedicoService.crearHistorial(paciente.getId());
        }

        return paciente;
    }

}

