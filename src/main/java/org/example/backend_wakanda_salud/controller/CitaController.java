package org.example.backend_wakanda_salud.controller;

import org.example.backend_wakanda_salud.service.citas.CitaService;
import org.example.backend_wakanda_salud.service.citas.NotificacionService;
import org.example.backend_wakanda_salud.service.citas.SistemaCitasService;
import org.example.backend_wakanda_salud.model.centroSalud.citas.CitaNormalDTO;
import org.example.backend_wakanda_salud.model.centroSalud.citas.CitaUrgenteDTO;
import org.example.backend_wakanda_salud.model.centroSalud.citas.NotificacionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private SistemaCitasService sistemaCitasService;

    @Autowired
    private NotificacionService notificacionService;

    // Obtener Cita Normal por ID
    @GetMapping("/normal/{citaId}")
    public CitaNormalDTO obtenerCitaNormalPorId(@PathVariable Long citaId) {
        return citaService.obtenerCitaNormalPorId(citaId);
    }

    // Obtener Cita Urgente por ID
    @GetMapping("/urgente/{citaId}")
    public CitaUrgenteDTO obtenerCitaUrgentePorId(@PathVariable Long citaId) {
        return citaService.obtenerCitaUrgentePorId(citaId);
    }

    // Aceptar Cita Urgente
    @PutMapping("/urgente/aceptar/{citaId}")
    public void aceptarCitaUrgente(@PathVariable Long citaId) {
        citaService.aceptarCitaUrgente(citaId);
    }

    // Obtener las notificaciones de un usuario
    @GetMapping("/notificaciones/{usuarioId}")
    public List<NotificacionDTO> obtenerNotificacionesDeUsuario(@PathVariable Long usuarioId) {
        return notificacionService.obtenerNotificacionesDeUsuario(usuarioId);
    }

    // Agregar una Cita Normal al sistema
    @PostMapping("/normal/{sistemaId}")
    public Long agregarCitaNormal(@PathVariable Long sistemaId, @RequestBody CitaNormalDTO citaNormalDTO) {
        return sistemaCitasService.agregarCitaNormal(sistemaId, citaNormalDTO);
    }

    // Agregar una Cita Urgente al sistema
    @PostMapping("/urgente/{sistemaId}")
    public Long agregarCitaUrgente(@PathVariable Long sistemaId, @RequestBody CitaUrgenteDTO citaUrgenteDTO) {
        return sistemaCitasService.agregarCitaUrgente(sistemaId, citaUrgenteDTO);
    }

    // Eliminar una Cita del sistema
    @DeleteMapping("/eliminar/{sistemaId}/{citaId}")
    public void eliminarCitaDeSistema(@PathVariable Long sistemaId, @PathVariable Long citaId) {
        sistemaCitasService.eliminarCitaDeSistema(sistemaId, citaId);
    }
}

