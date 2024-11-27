package org.example.backend_wakanda_salud.rest;


import org.example.backend_wakanda_salud.model.usuarios.pacientes.PacienteDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteResource {

    private final PacienteService pacienteService;

    public PacienteResource(final PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public ResponseEntity<List<PacienteDTO>> getAllPacientes() {
        return ResponseEntity.ok(pacienteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteDTO> getPaciente(@PathVariable final Long id) {
        return ResponseEntity.ok(pacienteService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createPaciente(@RequestBody final PacienteDTO pacienteDTO) {
        return ResponseEntity.ok(pacienteService.create(pacienteDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePaciente(@PathVariable final Long id, @RequestBody final PacienteDTO pacienteDTO) {
        pacienteService.update(id, pacienteDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaciente(@PathVariable final Long id) {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

