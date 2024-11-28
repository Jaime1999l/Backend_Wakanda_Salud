package org.example.backend_wakanda_salud.service.usuario.pacienteHistorial;

import org.example.backend_wakanda_salud.domain.usuarios.medicos.Medico;
import org.example.backend_wakanda_salud.domain.usuarios.pacientes.historialMedico.EntradaHistorial;
import org.example.backend_wakanda_salud.domain.usuarios.pacientes.historialMedico.HistorialMedico;
import org.example.backend_wakanda_salud.domain.usuarios.pacientes.Paciente;
import org.example.backend_wakanda_salud.model.usuarios.pacientes.historialMedico.EntradaHistorialDTO;
import org.example.backend_wakanda_salud.model.usuarios.pacientes.historialMedico.HistorialMedicoDTO;
import org.example.backend_wakanda_salud.repos.HistorialMedicoRepository;
import org.example.backend_wakanda_salud.repos.EntradaHistorialRepository;
import org.example.backend_wakanda_salud.repos.MedicoRepository;
import org.example.backend_wakanda_salud.repos.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistorialMedicoService {

    private final HistorialMedicoRepository historialMedicoRepository;
    private final EntradaHistorialRepository entradaHistorialRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public HistorialMedicoService(HistorialMedicoRepository historialMedicoRepository,
                                  EntradaHistorialRepository entradaHistorialRepository,
                                  PacienteRepository pacienteRepository,
                                  MedicoRepository medicoRepository) {
        this.historialMedicoRepository = historialMedicoRepository;
        this.entradaHistorialRepository = entradaHistorialRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    /**
     * Obtener el historial médico de un paciente por su ID.
     */
    @Transactional(readOnly = true)
    public HistorialMedicoDTO obtenerHistorialPorPacienteId(Long pacienteId) {
        HistorialMedico historial = historialMedicoRepository.findByPaciente_Id(pacienteId)
                .orElseThrow(() -> new RuntimeException("Historial médico no encontrado para el paciente."));
        return mapToDTO(historial);
    }

    @Transactional
    public Long agregarEntrada(Long medicoId, Long pacienteId, EntradaHistorialDTO entradaDTO) {
        // Obtener al paciente por su ID, si no se encuentra, lanzar una excepción
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado."));

        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado."));

        // Buscar el historial médico del paciente, si no existe, lanzar una excepción
        HistorialMedico historial = historialMedicoRepository.findByPaciente_Id(pacienteId)
                .orElseThrow(() -> new RuntimeException("Historial médico no encontrado para el paciente."));

        // Crear una nueva entrada en el historial médico
        EntradaHistorial entradaHistorial = new EntradaHistorial();
        // Asignar el ID de la entrada
        entradaHistorial.setDescripcion(entradaDTO.getDescripcion());  // Asignar la descripción de la entrada
        entradaHistorial.setFecha(entradaDTO.getFecha());  // Asignar la fecha de la entrada
        entradaHistorial.setMedico(medico);  // Aseguramos que 'medico' no sea nulo
        entradaHistorial.setHistorialMedico(historial);  // Asociar con el historial del paciente

        // Guardar la nueva entrada en la base de datos
        EntradaHistorial savedEntrada = entradaHistorialRepository.save(entradaHistorial);

        // Retornar el ID de la entrada guardada para poder usarlo más tarde (ej. para eliminarla)
        return savedEntrada.getId();
    }


    /**
     * Eliminar una entrada del historial médico.
     */
    @Transactional
    public void eliminarEntrada(Long entradaId) {
        try {
            EntradaHistorial entrada = entradaHistorialRepository.findById(entradaId)
                    .orElseThrow(() -> new RuntimeException("Entrada del historial no encontrada."));
            entradaHistorialRepository.delete(entrada);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la entrada del historial médico: " + e.getMessage(), e);
        }
    }

    /**
     * Crear un historial médico para un paciente.
     */
    @Transactional
    public void crearHistorial(Long pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado."));

        HistorialMedico historial = new HistorialMedico();
        historial.setPaciente(paciente);
        historial.setEntradas(List.of());
        historialMedicoRepository.save(historial);

        paciente.setHistorialMedico(historial);
        pacienteRepository.save(paciente);
    }

    /**
     * Mapear una entidad HistorialMedico a un DTO.
     */
    private HistorialMedicoDTO mapToDTO(HistorialMedico historial) {
        HistorialMedicoDTO dto = new HistorialMedicoDTO();
        dto.setId(historial.getId());
        dto.setPacienteId(historial.getPaciente().getId());
        dto.setEntradas(historial.getEntradas().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    /**
     * Mapear una entidad EntradaHistorial a un DTO.
     */
    private EntradaHistorialDTO mapToDTO(EntradaHistorial entrada) {
        EntradaHistorialDTO dto = new EntradaHistorialDTO();
        dto.setId(entrada.getId());
        dto.setFecha(entrada.getFecha());
        dto.setDescripcion(entrada.getDescripcion());
        dto.setPrescripciones(entrada.getPrescripciones());
        dto.setMedicoId(entrada.getMedico() != null ? entrada.getMedico().getId() : null);
        dto.setHistorialMedicoId(entrada.getHistorialMedico().getId());
        return dto;
    }
}
