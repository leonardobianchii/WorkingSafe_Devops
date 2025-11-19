package br.com.workingsafe.controller;

import br.com.workingsafe.dto.UsuarioDto;
import br.com.workingsafe.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UsuarioDto> criar(@RequestBody @Valid UsuarioDto dto) {
        UsuarioDto criado = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> buscarPorId(@PathVariable Long id) {
        UsuarioDto usuario = service.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioDto>> listarPorEmpresa(
            @RequestParam Long empresaId,
            Pageable pageable) {

        Page<UsuarioDto> pagina = service.listarPorEmpresa(empresaId, pageable);
        return ResponseEntity.ok(pagina);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioDto dto) {

        UsuarioDto atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
