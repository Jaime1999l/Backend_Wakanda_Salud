package org.example.backend_wakanda_salud.service.usuario;

import org.example.backend_wakanda_salud.domain.centroSalud.CentroSalud;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.Medico;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.AgendaMedica;
import org.example.backend_wakanda_salud.model.usuarios.medicos.MedicoDTO;
import org.example.backend_wakanda_salud.repos.CentroSaludRepository;
import org.example.backend_wakanda_salud.repos.MedicoRepository;
import org.example.backend_wakanda_salud.repos.AgendaMedicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private AgendaMedicaRepository agendaMedicaRepository;

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CentroSaludRepository centroSaludRepository;

    // CRUD

    @Transactional
    public Long create(MedicoDTO medicoDTO) {
        // Verificar si el usuario será un médico
        if (medicoDTO.getRoles().contains("MEDICO")) {
            // Crear la entidad específica del médico
            Medico medico = new Medico();
            mapToEntity(medicoDTO, medico);

            // Validar y asignar especialidad
            if (medicoDTO.getEspecialidad() == null || medicoDTO.getEspecialidad().isEmpty()) {
                medico.setEspecialidad(generarEspecialidadAleatoria());
            } else {
                medico.setEspecialidad(medicoDTO.getEspecialidad());
            }

            // Validar y asignar número de licencia
            if (medicoDTO.getNumeroLicencia() == null || medicoDTO.getNumeroLicencia().isEmpty()) {
                medico.setNumeroLicencia(generarNumeroLicenciaAleatorio());
            } else {
                medico.setNumeroLicencia(medicoDTO.getNumeroLicencia());
            }

            // Configurar agenda médica
            if (medico.getAgenda() == null) {
                AgendaMedica agenda = new AgendaMedica();
                medico.setAgenda(agenda);

                // Generar disponibilidad aleatoria para la agenda
                usuarioService.generarDisponibilidadAleatoria(agenda);
            }

            if (medico.getCentroSalud() == null) {
                List<CentroSalud> centrosDisponibles = centroSaludRepository.findAll();
                if (!centrosDisponibles.isEmpty()) {
                    CentroSalud centroAleatorio = centrosDisponibles.get((int) (Math.random() * centrosDisponibles.size()));
                    medico.setCentroSalud(centroAleatorio);
                } else {
                    throw new RuntimeException("No hay centros de salud disponibles para asignar.");
                }
            }
            return medicoRepository.save(medico).getId();
        }

        // Si no tiene rol válido, lanzamos excepción
        throw new RuntimeException("El usuario debe tener el rol de: MEDICO");
    }

    public String generarEspecialidadAleatoria() {
        List<String> especialidades = List.of(
                "Cardiología", "Pediatría", "Neurología", "Dermatología",
                "Ginecología", "Ortopedia", "Oncología", "Psiquiatría",
                "Oftalmología", "Urología", "Endocrinología", "Anestesiología"
        );
        int indiceAleatorio = ThreadLocalRandom.current().nextInt(especialidades.size());
        return especialidades.get(indiceAleatorio);
    }

    public String generarNumeroLicenciaAleatorio() {
        String prefijo = "LIC-";
        int numero = ThreadLocalRandom.current().nextInt(100000, 999999);
        return prefijo + numero;
    }

    @Transactional(readOnly = true)
    public MedicoDTO get(Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
        return mapToDTO(medico);
    }

    @Transactional(readOnly = true)
    public List<MedicoDTO> findAll() {
        return medicoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(Long id, MedicoDTO medicoDTO) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
        mapToEntity(medicoDTO, medico);
        medicoRepository.save(medico);
    }

    @Transactional
    public void delete(Long id) {
        medicoRepository.deleteById(id);
    }

    // Métodos de mapeo

    public MedicoDTO mapToDTO(Medico medico) {
        MedicoDTO dto = new MedicoDTO();
        dto.setId(medico.getId());
        dto.setEspecialidad(medico.getEspecialidad());
        dto.setNumeroLicencia(medico.getNumeroLicencia());
        dto.setAgendaId(medico.getAgenda() != null ? medico.getAgenda().getId() : null);
        return dto;
    }

    public Medico mapToEntity(MedicoDTO dto, Medico medico) {
        medico.setEspecialidad(dto.getEspecialidad());
        medico.setNumeroLicencia(dto.getNumeroLicencia());
        return medico;
    }
}

