package com.neurofocus.neurofocus.Controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.neurofocus.neurofocus.Models.Usuario;
import com.neurofocus.neurofocus.Repository.UsuarioRepository;

@Controller
public class DashboardController {
    
    private final UsuarioRepository usuarioRepository;
    
    public DashboardController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    @GetMapping("/dashboard-redirect")
    public String redirectByRole(Authentication auth, Model model) {
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        
        if (usuario == null) return "redirect:/";
        
        model.addAttribute("usuario", usuario);
        
        switch (usuario.getRol()) {
            case "PACIENTE":
                return "login/usuario/dashboard";
            case "PSICOLOGO":
                return "login/psicologo/dashboard";
            case "ADMIN":
                return "login/administrador/dashboard";
            default:
                return "redirect:/";
        }
    }
}