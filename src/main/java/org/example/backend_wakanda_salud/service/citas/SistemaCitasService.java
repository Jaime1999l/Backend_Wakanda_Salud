package org.example.backend_wakanda_salud.service.citas;

import org.example.backend_wakanda_salud.domain.centroSalud.CentroSalud;
import org.example.backend_wakanda_salud.domain.centroSalud.SistemaCitas;
import org.example.backend_wakanda_salud.domain.centroSalud.citas.Cita;
import org.example.backend_wakanda_salud.domain.centroSalud.citas.CitaNormal;
import org.example.backend_wakanda_salud.domain.centroSalud.citas.CitaUrgente;
import org.example.backend_wakanda_salud.model.centroSalud.citas.CitaNormalDTO;
import org.example.backend_wakanda_salud.model.centroSalud.citas.CitaUrgenteDTO;
import org.example.backend_wakanda_salud.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SistemaCitasService {

    @Autowired
    private CentroSaludRepository centroSaludRepository;

    private final SistemaCitasRepository sistemaCitasRepository;
    private final CitaNormalRepository citaNormalRepository;
    private final CitaUrgenteRepository citaUrgenteRepository;
    private final NotificacionService notificacionService;

    public SistemaCitasService(SistemaCitasRepository sistemaCitasRepository,
                               CitaNormalRepository citaNormalRepository,
                               CitaUrgenteRepository citaUrgenteRepository,
                               NotificacionService notificacionService) {
        this.sistemaCitasRepository = sistemaCitasRepository;
        this.citaNormalRepository = citaNormalRepository;
        this.citaUrgenteRepository = citaUrgenteRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public Long crearSistemaCitas(String descripcion, Long centroSaludId) {
        // Buscar el centro de salud asociado por su ID
        CentroSalud centroSalud = centroSaludRepository.findById(centroSaludId)
                .orElseThrow(() -> new RuntimeException("Centro de salud no encontrado."));

        // Crear una nueva instancia de SistemaCitas
        SistemaCitas sistema = new SistemaCitas();
        sistema.setDescripcion(descripcion);
        sistema.setCentroSalud(centroSalud); // Asociar el centro de salud existente

        // Guardar el sistema de citas y retornar su ID
        return sistemaCitasRepository.save(sistema).getId();
    }

    // Obtener todas las citas (normales y urgentes) de un sistema
    @Transactional(readOnly = true)
    public List<Cita> obtenerCitasDeSistema(Long sistemaId) {
        SistemaCitas sistema = sistemaCitasRepository.findById(sistemaId)
                .orElseThrow(() -> new RuntimeException("Sistema de citas no encontrado."));
        return sistema.getCitas();
    }

    // Agregar una cita normal al sistema
    @Transactional
    public Long agregarCitaNormal(Long sistemaId, CitaNormalDTO citaNormalDTO, CitaService citaService) {
        SistemaCitas sistema = sistemaCitasRepository.findById(sistemaId)
                .orElseThrow(() -> new RuntimeException("Sistema de citas no encontrado."));

        Long citaId = citaService.crearCitaNormal(citaNormalDTO);
        CitaNormal cita = citaNormalRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Error al asociar la cita normal."));

        sistema.getCitas().add(cita);
        sistemaCitasRepository.save(sistema);
        return citaId;
    }

    // Agregar una cita urgente al sistema
    @Transactional
    public Long agregarCitaUrgente(Long sistemaId, CitaUrgenteDTO citaUrgenteDTO, CitaService citaService) {
        SistemaCitas sistema = sistemaCitasRepository.findById(sistemaId)
                .orElseThrow(() -> new RuntimeException("Sistema de citas no encontrado."));

        Long citaId = citaService.crearCitaUrgente(citaUrgenteDTO);
        CitaUrgente cita = citaUrgenteRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Error al asociar la cita urgente."));

        sistema.getCitas().add(cita);
        sistemaCitasRepository.save(sistema);
        return citaId;
    }

    // Eliminar una cita de un sistema
    @Transactional
    public void eliminarCitaDeSistema(Long sistemaId, Long citaId, CitaService citaService) {
        SistemaCitas sistema = sistemaCitasRepository.findById(sistemaId)
                .orElseThrow(() -> new RuntimeException("Sistema de citas no encontrado."));

        Cita cita = citaService.obtenerCitaPorId(citaId);
        sistema.getCitas().remove(cita);
        citaService.eliminarCita(citaId);
    }
}
