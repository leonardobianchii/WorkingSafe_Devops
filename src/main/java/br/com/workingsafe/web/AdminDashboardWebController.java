package br.com.workingsafe.web;

import br.com.workingsafe.dto.AggIndiceSemanalDto;
import br.com.workingsafe.dto.UsuarioDto;
import br.com.workingsafe.service.AggIndiceSemanalService;
import br.com.workingsafe.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminDashboardWebController {

    private final AggIndiceSemanalService aggService;
    private final UsuarioService usuarioService;

    public AdminDashboardWebController(AggIndiceSemanalService aggService,
                                       UsuarioService usuarioService) {
        this.aggService = aggService;
        this.usuarioService = usuarioService;
    }

    /**
     * Painel inicial do GESTOR. Mostra KPIs + agregados semanais.
     */
    @GetMapping("/admin/dashboard")
    public String dashboard(Authentication authentication, Model model) {

        // gestor logado
        String email = authentication.getName();
        UsuarioDto gestor = usuarioService.buscarPorEmail(email);
        Long empresaId = gestor.empresaId();

        // KPIs usando o AggIndiceSemanalService
        Double kpiMediaIndice = aggService.calcularMediaIndiceSemanaAtual(empresaId);
        Double kpiMediaRisco  = aggService.calcularMediaRiscoSemanaAtual(empresaId);
        Integer kpiAdesao     = aggService.calcularAdesaoSemanaAtual(empresaId);

        // últimas 6 semanas da empresa
        List<AggIndiceSemanalDto> aggSemanais =
                aggService.listarUltimasSemanasPorEmpresa(empresaId, 6);

        model.addAttribute("kpiMediaIndice", kpiMediaIndice);
        model.addAttribute("kpiMediaRisco", kpiMediaRisco);
        model.addAttribute("kpiAdesao", kpiAdesao);

        model.addAttribute("aggSemanais", aggSemanais);

        return "admin/dashboard";
    }
}
