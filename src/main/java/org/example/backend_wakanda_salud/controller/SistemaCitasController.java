package org.example.backend_wakanda_salud.controller;

import org.example.backend_wakanda_salud.domain.centroSalud.citas.Cita;
import org.example.backend_wakanda_salud.service.citas.SistemaCitasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sistemas-citas")
public class SistemaCitasController {

    @Autowired
    private SistemaCitasService sistemaCitasService;

    // Obtener todas las citas de un sistema
    @GetMapping("/{sistemaId}/citas")
    public List<Cita> obtenerCitasDeSistema(@PathVariable Long sistemaId) {
        return sistemaCitasService.obtenerCitasDeSistema(sistemaId);
    }

    // Eliminar una cita de un sistema
    @DeleteMapping("/{sistemaId}/citas/{citaId}")
    public void eliminarCitaDeSistema(@PathVariable Long sistemaId, @PathVariable Long citaId) {
        sistemaCitasService.eliminarCitaDeSistema(sistemaId, citaId);
    }
}
