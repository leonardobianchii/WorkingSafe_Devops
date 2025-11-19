package br.com.workingsafe.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // opcional: ao acessar "/", manda para /login
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    // PÁGINA DE LOGIN
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";   // templates/auth/login.html
    }

    // PÁGINA DE ACESSO NEGADO
    @GetMapping("/403")
    public String accessDenied() {
        return "auth/403";     // templates/auth/403.html
    }

    // *** IMPORTANTE: NÃO TER NADA COM @GetMapping("/home") AQUI ***
}
