package br.com.workingsafe.controller;

import br.com.workingsafe.dto.TimeEquipeDto;
import br.com.workingsafe.service.TimeEquipeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/times")
public class TimeEquipeController {

    private final TimeEquipeService service;

    public TimeEquipeController(TimeEquipeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TimeEquipeDto> criar(@RequestBody @Valid TimeEquipeDto dto) {
        TimeEquipeDto criado = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeEquipeDto> buscarPorId(@PathVariable Long id) {
        TimeEquipeDto time = service.buscarPorId(id);
        return ResponseEntity.ok(time);
    }

    @GetMapping
    public ResponseEntity<Page<TimeEquipeDto>> listarPorEmpresa(
            @RequestParam Long empresaId,
            Pageable pageable) {

        Page<TimeEquipeDto> pagina = service.listarPorEmpresa(empresaId, pageable);
        return ResponseEntity.ok(pagina);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeEquipeDto> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid TimeEquipeDto dto) {

        TimeEquipeDto atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
