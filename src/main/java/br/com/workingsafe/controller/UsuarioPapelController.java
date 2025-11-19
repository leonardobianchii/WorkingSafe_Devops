package br.com.workingsafe.controller;

import br.com.workingsafe.dto.UsuarioPapelDto;
import br.com.workingsafe.service.UsuarioPapelService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios-papeis")
public class UsuarioPapelController {

    private final UsuarioPapelService service;

    public UsuarioPapelController(UsuarioPapelService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UsuarioPapelDto> atribuirPapel(@RequestBody @Valid UsuarioPapelDto dto) {
        UsuarioPapelDto criado = service.atribuirPapel(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<UsuarioPapelDto>> listarPapeisDoUsuario(
            @PathVariable Long usuarioId,
            Pageable pageable) {

        Page<UsuarioPapelDto> pagina = service.listarPapeisDoUsuario(usuarioId, pageable);
        return ResponseEntity.ok(pagina);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerPapel(@PathVariable Long id) {
        service.removerPapel(id);
        return ResponseEntity.noContent().build();
    }
}
