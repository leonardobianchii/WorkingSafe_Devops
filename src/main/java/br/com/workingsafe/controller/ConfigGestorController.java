package br.com.workingsafe.controller;

import br.com.workingsafe.dto.ConfigGestorDto;
import br.com.workingsafe.service.ConfigGestorService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config-gestor")
public class ConfigGestorController {

    private final ConfigGestorService service;

    public ConfigGestorController(ConfigGestorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConfigGestorDto> criar(@RequestBody @Valid ConfigGestorDto dto) {
        ConfigGestorDto criado = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConfigGestorDto> buscarPorId(@PathVariable Long id) {
        ConfigGestorDto dto = service.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<ConfigGestorDto> buscarPorEscopo(
            @RequestParam Long empresaId,
            @RequestParam(required = false) Long timeId) {

        ConfigGestorDto dto = service.buscarPorEscopo(empresaId, timeId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfigGestorDto> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ConfigGestorDto dto) {

        ConfigGestorDto atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
