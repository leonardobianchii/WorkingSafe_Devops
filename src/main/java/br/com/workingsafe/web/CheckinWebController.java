package br.com.workingsafe.web;

import br.com.workingsafe.dto.CheckinDto;
import br.com.workingsafe.dto.UsuarioDto;
import br.com.workingsafe.service.CheckinService;
import br.com.workingsafe.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class CheckinWebController {

    private final UsuarioService usuarioService;
    private final CheckinService checkinService;

    public CheckinWebController(UsuarioService usuarioService,
                                CheckinService checkinService) {
        this.usuarioService = usuarioService;
        this.checkinService = checkinService;
    }

    @PostMapping("/view/checkins")
    public String registrarCheckin(Authentication authentication,
                                   @ModelAttribute("checkinForm")
                                   HomeWebController.CheckinFormViewModel form,
                                   RedirectAttributes redirectAttributes) {

        String email = authentication.getName();
        UsuarioDto usuario = usuarioService.buscarPorEmail(email);

        // monta o DTO com os dados do formulário
        CheckinDto dto = new CheckinDto(
                null,                     // id
                usuario.id(),             // usuário logado
                LocalDateTime.now(),                     // dataHora -> deixa o mapper/service preencher com "agora"
                form.getHumor(),
                form.getFoco(),
                form.getMinutosPausas(),
                form.getHorasTrabalhadas(),
                form.getObservacoes(),
                form.getTags(),
                "WEB"                     // origem
        );

        checkinService.criar(dto);

        redirectAttributes.addFlashAttribute(
                "msgSucesso",
                "Check-in registrado com sucesso! Obrigado por compartilhar como você está."
        );

        return "redirect:/home";
    }
}
