package br.com.workingsafe.controller;

import br.com.workingsafe.dto.AggIndiceSemanalDto;
import br.com.workingsafe.service.AggIndiceSemanalService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboards/semanal")
public class AggIndiceSemanalController {

    private final AggIndiceSemanalService service;

    public AggIndiceSemanalController(AggIndiceSemanalService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AggIndiceSemanalDto> criar(@RequestBody @Valid AggIndiceSemanalDto dto) {
        AggIndiceSemanalDto criado = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AggIndiceSemanalDto> buscarPorId(@PathVariable Long id) {
        AggIndiceSemanalDto dto = service.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<AggIndiceSemanalDto>> listarPorEmpresaEPeriodo(
            @RequestParam Long empresaId,
            @RequestParam(required = false) Long timeId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Pageable pageable) {

        Page<AggIndiceSemanalDto> pagina = service.listarPorEmpresaEPeriodo(
                empresaId, timeId, from, to, pageable
        );
        return ResponseEntity.ok(pagina);
    }
}
