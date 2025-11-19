package br.com.workingsafe.controller;

import br.com.workingsafe.dto.EmpresaDto;
import br.com.workingsafe.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService service;

    public EmpresaController(EmpresaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EmpresaDto> criar(@RequestBody @Valid EmpresaDto dto) {
        EmpresaDto criada = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDto> buscarPorId(@PathVariable Long id) {
        EmpresaDto empresa = service.buscarPorId(id);
        return ResponseEntity.ok(empresa);
    }

    @GetMapping
    public ResponseEntity<Page<EmpresaDto>> listar(Pageable pageable) {
        Page<EmpresaDto> pagina = service.listar(pageable);
        return ResponseEntity.ok(pagina);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaDto> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid EmpresaDto dto) {

        EmpresaDto atualizada = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
