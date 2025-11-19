package br.com.workingsafe.controller;

import br.com.workingsafe.dto.CheckinDto;
import br.com.workingsafe.service.CheckinService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/checkins")
public class CheckinController {

    private final CheckinService service;

    public CheckinController(CheckinService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CheckinDto> criar(@RequestBody @Valid CheckinDto dto) {
        CheckinDto criado = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckinDto> buscarPorId(@PathVariable Long id) {
        CheckinDto checkin = service.buscarPorId(id);
        return ResponseEntity.ok(checkin);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<CheckinDto>> listarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Pageable pageable) {

        Page<CheckinDto> pagina = service.listarPorUsuarioEPeriodo(usuarioId, from, to, pageable);
        return ResponseEntity.ok(pagina);
    }
}
