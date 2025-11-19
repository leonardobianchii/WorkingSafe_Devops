package br.com.workingsafe.controller;

import br.com.workingsafe.dto.RecomendacaoIaDto;
import br.com.workingsafe.service.RecomendacaoIaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recomendacoes")
public class RecomendacaoIaController {

    private final RecomendacaoIaService service;

    public RecomendacaoIaController(RecomendacaoIaService service) {
        this.service = service;
    }

    // Gera novas recomendacoes com base nos ultimos 7 dias de check-in
    @PostMapping("/gerar")
    public ResponseEntity<List<RecomendacaoIaDto>> gerar(@RequestParam Long usuarioId) {
        List<RecomendacaoIaDto> recs = service.gerarRecomendacoesGenericas(usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(recs);
    }

    // Lista apenas as recomendacoes ainda validas
    @GetMapping("/ativas")
    public ResponseEntity<List<RecomendacaoIaDto>> listarAtivas(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(service.listarAtivasPorUsuario(usuarioId));
    }

    // (Opcional) Lista todas as recomendacoes historicas
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<RecomendacaoIaDto>> listarTodas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.listarTodasPorUsuario(usuarioId));
    }
}
