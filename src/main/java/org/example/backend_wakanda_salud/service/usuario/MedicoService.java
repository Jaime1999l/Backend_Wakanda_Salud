package org.example.backend_wakanda_salud.service.usuario;

import org.example.backend_wakanda_salud.domain.centroSalud.CentroSalud;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.Medico;
import org.example.backend_wakanda_salud.domain.usuarios.medicos.AgendaMedica;
import org.example.backend_wakanda_salud.model.usuarios.medicos.MedicoDTO;
import org.example.backend_wakanda_salud.repos.CentroSaludRepository;
import org.example.backend_wakanda_salud.repos.MedicoRepository;
import org.example.backend_wakanda_salud.repos.AgendaMedicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private CentroSaludRepository centroSaludRepository;

    private final UsuarioService usuarioService;

    public MedicoService(@Lazy UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Long create(MedicoDTO medicoDTO) {
        if (medicoDTO.getRoles().contains("MEDICO")) {
            Medico medico = new Medico();
            mapToEntity(medicoDTO, medico);

            if (medicoDTO.getEspecialidad() == null || medicoDTO.getEspecialidad().isEmpty()) {
                medico.setEspecialidad(generarEspecialidadAleatoria());
            } else {
                medico.setEspecialidad(medicoDTO.getEspecialidad());
            }

            if (medicoDTO.getNumeroLicencia() == null || medicoDTO.getNumeroLicencia().isEmpty()) {
                medico.setNumeroLicencia(generarNumeroLicenciaAleatorio());
            } else {
                medico.setNumeroLicencia(medicoDTO.getNumeroLicencia());
            }

            Medico medicoGuardado = medicoRepository.save(medico);

            AgendaMedica agenda = new AgendaMedica();
            agenda.setMedico(medicoGuardado);
            AgendaMedica agendaGuardada = agendaMedicaRepository.save(agenda);

            medicoGuardado.setAgenda(agendaGuardada);
            medicoRepository.save(medicoGuardado);

            usuarioService.generarDisponibilidadAleatoria(agendaGuardada);

            if (medico.getCentroSalud() == null) {
                List<CentroSalud> centrosDisponibles = centroSaludRepository.findAll();
                if (!centrosDisponibles.isEmpty()) {
                    CentroSalud centroAleatorio = centrosDisponibles.get((int) (Math.random() * centrosDisponibles.size()));
                    medico.setCentroSalud(centroAleatorio);
                } else {
                    throw new RuntimeException("No hay centros de salud disponibles para asignar.");
                }
            }
            return medicoGuardado.getId();
        }

        throw new RuntimeException("El usuario debe tener el rol de: MEDICO");
    }

    public String generarEspecialidadAleatoria() {
        List<String> especialidades = List.of(
                "Cardiología", "Pediatría", "Neurología", "Dermatología",
                "Ginecología", "Traumatología", "Oncología", "Psiquiatría",
                "Oftalmología", "Urología", "Endocrinología", "Anestesiología",
                "Reumatología", "Geriatría", "Nefrología", "Hematología",
                "Medicina Interna", "Medicina General", "Cirugía General",
                "Cirugía Plástica", "Cirugía Cardiovascular", "Cirugía Pediátrica"
        );
        return especialidades.get(ThreadLocalRandom.current().nextInt(especialidades.size()));
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
