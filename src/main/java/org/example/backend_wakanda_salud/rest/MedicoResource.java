package org.example.backend_wakanda_salud.rest;

import org.example.backend_wakanda_salud.model.usuarios.medicos.MedicoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
public class MedicoResource {

    private final MedicoService medicoService;

    public MedicoResource(final MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @GetMapping
    public ResponseEntity<List<MedicoDTO>> getAllMedicos() {
        return ResponseEntity.ok(medicoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicoDTO> getMedico(@PathVariable final Long id) {
        return ResponseEntity.ok(medicoService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createMedico(@RequestBody final MedicoDTO medicoDTO) {
        return ResponseEntity.ok(medicoService.create(medicoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMedico(@PathVariable final Long id, @RequestBody final MedicoDTO medicoDTO) {
        medicoService.update(id, medicoDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedico(@PathVariable final Long id) {
        medicoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

