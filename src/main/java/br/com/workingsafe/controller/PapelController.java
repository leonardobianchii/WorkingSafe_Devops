package br.com.workingsafe.controller;

import br.com.workingsafe.dto.PapelDto;
import br.com.workingsafe.service.PapelService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/papeis")
public class PapelController {

    private final PapelService service;

    public PapelController(PapelService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PapelDto> criar(@RequestBody @Valid PapelDto dto) {
        PapelDto criado = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PapelDto> buscarPorId(@PathVariable Long id) {
        PapelDto papel = service.buscarPorId(id);
        return ResponseEntity.ok(papel);
    }

    @GetMapping
    public ResponseEntity<Page<PapelDto>> listar(Pageable pageable) {
        Page<PapelDto> pagina = service.listar(pageable);
        return ResponseEntity.ok(pagina);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PapelDto> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid PapelDto dto) {

        PapelDto atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
