package br.com.workingsafe.web;

import br.com.workingsafe.dto.UsuarioDto;
import br.com.workingsafe.service.EmpresaService;
import br.com.workingsafe.service.TimeEquipeService;
import br.com.workingsafe.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioWebController {

    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;
    private final TimeEquipeService timeEquipeService;

    public AdminUsuarioWebController(UsuarioService usuarioService,
                                     EmpresaService empresaService,
                                     TimeEquipeService timeEquipeService) {
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
        this.timeEquipeService = timeEquipeService;
    }

    /**
     * Lista usuários da empresa do gestor logado.
     */
    @GetMapping
    public String listar(Authentication authentication, Model model) {
        String emailGestor = authentication.getName();
        UsuarioDto gestor = usuarioService.buscarPorEmail(emailGestor);

        List<UsuarioDto> usuarios = usuarioService.listarPorEmpresaSemPaginacao(gestor.empresaId());

        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios-list";
    }

    /**
     * Tela de criação de usuário.
     */
    @GetMapping("/novo")
    public String novo(Authentication authentication, Model model) {
        String emailGestor = authentication.getName();
        UsuarioDto gestor = usuarioService.buscarPorEmail(emailGestor);

        UsuarioDto dto = new UsuarioDto(
                null,                // id
                gestor.empresaId(),  // empresaId (empresa do gestor)
                null,                // timeId
                "",                  // nome
                "",                  // email
                null,                // fusoHorario
                true,                // ativo
                null,                // empresaNome (apenas visual, não precisa no form)
                null                 // timeNome (apenas visual)
        );

        model.addAttribute("tituloPagina", "Novo usuário");
        model.addAttribute("usuario", dto);
        model.addAttribute("empresas", empresaService.listarTodas());
        model.addAttribute("times", timeEquipeService.listarPorEmpresa(gestor.empresaId()));
        model.addAttribute("acao", "/admin/usuarios/novo");

        return "admin/usuario-form";
    }

    /**
     * POST de criação.
     */
    @PostMapping("/novo")
    public String criar(@ModelAttribute("usuario") UsuarioDto dto) {
        usuarioService.criar(dto);
        return "redirect:/admin/usuarios";
    }

    /**
     * Tela de edição.
     */
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        UsuarioDto dto = usuarioService.buscarPorId(id);

        model.addAttribute("tituloPagina", "Editar usuário");
        model.addAttribute("usuario", dto);
        model.addAttribute("empresas", empresaService.listarTodas());
        model.addAttribute("times", timeEquipeService.listarPorEmpresa(dto.empresaId()));
        model.addAttribute("acao", "/admin/usuarios/" + id + "/editar");

        return "admin/usuario-form";
    }

    /**
     * POST de edição.
     */
    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id,
                            @ModelAttribute("usuario") UsuarioDto dto) {
        usuarioService.atualizar(id, dto);
        return "redirect:/admin/usuarios";
    }

    /**
     * Exclusão.
     */
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        usuarioService.excluir(id);   // esse já está fazendo delete de verdade
        return "redirect:/admin/usuarios";
    }
}
