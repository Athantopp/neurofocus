package com.neurofocus.neurofocus.Controllers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.neurofocus.neurofocus.Models.Usuario;
import com.neurofocus.neurofocus.Repository.UsuarioRepository;

@Controller
public class AuthController {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("/login")
    public String login() { return "login/usuario/login"; }
    @GetMapping("/registro")
    public String registroForm() { return "login/usuario/registro"; }
    @PostMapping("/registro")
    public String registrar(@RequestParam String nombre, @RequestParam String email,
                           @RequestParam String password, @RequestParam String rol) {
        if(usuarioRepository.existsByEmail(email)) return "redirect:/registro?error";
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol.toUpperCase());
        usuarioRepository.save(usuario);
        return "redirect:/login";
    }
    @GetMapping("/dashboard-redirect")
    public String redirectDashboard() { return "redirect:/"; }
}