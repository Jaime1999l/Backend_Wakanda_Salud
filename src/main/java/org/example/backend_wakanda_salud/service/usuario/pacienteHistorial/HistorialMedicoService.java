package org.example.backend_wakanda_salud.service.usuario.pacienteHistorial;

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

    /**
     * Agregar una entrada al historial médico de un paciente.
     */
    @Transactional
    public void agregarEntrada(Long pacienteId, EntradaHistorialDTO entradaDTO) {
        HistorialMedico historial = historialMedicoRepository.findByPaciente_Id(pacienteId)
                .orElseThrow(() -> new RuntimeException("Historial médico no encontrado para el paciente."));

        EntradaHistorial entrada = new EntradaHistorial();
        entrada.setFecha(entradaDTO.getFecha());
        entrada.setDescripcion(entradaDTO.getDescripcion());
        entrada.setPrescripciones(entradaDTO.getPrescripciones());

        // Verificar y asignar el médico
        if (entradaDTO.getMedicoId() != null) {
            entrada.setMedico(medicoRepository.findById(entradaDTO.getMedicoId())
                    .orElseThrow(() -> new RuntimeException("Médico no encontrado.")));
        }

        entrada.setHistorialMedico(historial);
        historial.getEntradas().add(entrada);
        historialMedicoRepository.save(historial);
    }

    /**
     * Eliminar una entrada del historial médico.
     */
    @Transactional
    public void eliminarEntrada(Long entradaId) {
        EntradaHistorial entrada = entradaHistorialRepository.findById(entradaId)
                .orElseThrow(() -> new RuntimeException("Entrada del historial no encontrada."));
        entradaHistorialRepository.delete(entrada);
    }

    /**
     * Actualizar una entrada del historial médico.
     */
    @Transactional
    public void actualizarEntrada(Long entradaId, EntradaHistorialDTO entradaDTO) {
        EntradaHistorial entrada = entradaHistorialRepository.findById(entradaId)
                .orElseThrow(() -> new RuntimeException("Entrada del historial no encontrada."));

        entrada.setFecha(entradaDTO.getFecha());
        entrada.setDescripcion(entradaDTO.getDescripcion());
        entrada.setPrescripciones(entradaDTO.getPrescripciones());

        // Verificar y asignar el médico si es necesario
        if (entradaDTO.getMedicoId() != null) {
            entrada.setMedico(medicoRepository.findById(entradaDTO.getMedicoId())
                    .orElseThrow(() -> new RuntimeException("Médico no encontrado.")));
        }

        entradaHistorialRepository.save(entrada);
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
     * Eliminar un historial médico.
     */
    @Transactional
    public void eliminarHistorial(Long pacienteId) {
        HistorialMedico historial = historialMedicoRepository.findByPaciente_Id(pacienteId)
                .orElseThrow(() -> new RuntimeException("Historial médico no encontrado para el paciente."));
        historialMedicoRepository.delete(historial);
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
