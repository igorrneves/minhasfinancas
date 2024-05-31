package br.com.ireis.minhasfinancas;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeControle {
    @GetMapping("/login")
    private String carregaPaginaLogin(){
        return "/login";
    }
    @GetMapping("/home")
    private String carregaPaginaHome(){
        return "/home";
    }

    @PostMapping("/logout")
    private String realizaLogout(){
         return "redirect:/login?logout";
    }
}
