package org.example.backend_wakanda_salud.controller;

import org.example.backend_wakanda_salud.service.usuario.pacienteHistorial.HistorialMedicoService;
import org.example.backend_wakanda_salud.model.usuarios.pacientes.historialMedico.EntradaHistorialDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/historiales")
public class HistorialMedicoController {

    @Autowired
    private HistorialMedicoService historialMedicoService;

    // Agregar una entrada al historial médico
    @PostMapping("/entrada/{medicoId}/{pacienteId}")
    public Long agregarEntrada(
            @PathVariable Long medicoId,
            @PathVariable Long pacienteId,
            @RequestBody EntradaHistorialDTO entradaDTO) {
        return historialMedicoService.agregarEntrada(medicoId, pacienteId, entradaDTO);
    }

    // Eliminar una entrada del historial médico
    @DeleteMapping("/entrada/{entradaId}")
    public void eliminarEntrada(@PathVariable Long entradaId) {
        historialMedicoService.eliminarEntrada(entradaId);
    }
}
