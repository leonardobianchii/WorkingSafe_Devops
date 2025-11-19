package br.com.workingsafe.config;

import br.com.workingsafe.model.Papel;
import br.com.workingsafe.model.Usuario;
import br.com.workingsafe.model.UsuarioPapel;
import br.com.workingsafe.repository.UsuarioPapelRepository;
import br.com.workingsafe.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // DEV: senha em texto puro (gestor123 / colab123)
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository,
                                                 UsuarioPapelRepository usuarioPapelRepository) {

        return username -> {
            Usuario usuario = usuarioRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

            if (!"S".equalsIgnoreCase(usuario.getAtivo())) {
                throw new UsernameNotFoundException("Usuário inativo.");
            }

            List<UsuarioPapel> relacoes = usuarioPapelRepository.findByUsuarioId(usuario.getId());

            List<String> roles = relacoes.stream()
                    .map(UsuarioPapel::getPapel)
                    .map(Papel::getCodigo) // GESTOR, COLABORADOR...
                    .collect(Collectors.toList());

            if (roles.isEmpty()) {
                throw new UsernameNotFoundException("Usuário sem papéis configurados.");
            }

            // senha fixa por papel
            String rawPassword = roles.contains("GESTOR") ? "gestor123" : "colab123";

            return User.withUsername(usuario.getEmail())
                    .password(rawPassword)
                    .roles(roles.toArray(new String[0])) // vira ROLE_GESTOR / ROLE_COLABORADOR
                    .build();
        };
    }

    // -------------------------
    // Handler de sucesso no login
    // -------------------------
    @Bean
    public AuthenticationSuccessHandler customAuthSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isGestor = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority) // ex: ROLE_GESTOR
                    .anyMatch(r -> r.equals("ROLE_GESTOR"));

            if (isGestor) {
                // gestor → painel admin
                response.sendRedirect("/admin/dashboard");
            } else {
                // colaborador → home colaborador
                response.sendRedirect("/home");
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/login",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**",
                        "/error",
                        "/h2-console/**"
                ).permitAll()
                .anyRequest().authenticated()
        );

        // liberar frames do H2
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        // LOGIN usando nosso success handler
        http.formLogin(login -> login
                .loginPage("/login")          // GET /login -> mostra HTML
                .loginProcessingUrl("/login") // POST /login -> autentica
                .successHandler(customAuthSuccessHandler())  // << AQUI
                .failureUrl("/login?error=true")
                .permitAll()
        );

        // LOGOUT
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );

        http.exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        http.csrf(Customizer.withDefaults());

        return http.build();
    }
}
