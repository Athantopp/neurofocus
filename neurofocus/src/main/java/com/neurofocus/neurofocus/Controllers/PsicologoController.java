package com.neurofocus.neurofocus.Controllers;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.neurofocus.neurofocus.Models.Especialista;
import com.neurofocus.neurofocus.Models.Paciente;
import com.neurofocus.neurofocus.Models.Usuario;
import com.neurofocus.neurofocus.Repository.EspecialistaRepository;
import com.neurofocus.neurofocus.Repository.PacienteRepository;
import com.neurofocus.neurofocus.Repository.UsuarioRepository;

@Controller
@RequestMapping("/psicologo")
public class PsicologoController {

    private final EspecialistaRepository especialistaRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    public PsicologoController(EspecialistaRepository especialistaRepository,
                               PacienteRepository pacienteRepository,
                               UsuarioRepository usuarioRepository) {
        this.especialistaRepository = especialistaRepository;
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model){
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if(usuario != null){
            Especialista psicologo = especialistaRepository.findByUsuarioId(usuario.getId());
            model.addAttribute("psicologo",psicologo);
        }
        return "login/psicologo/dashboard";
    }

    @GetMapping("/mis-pacientes")
    public String misPacientes(Authentication auth, Model model) {
        String email = auth.getName();
        System.out.println("=== MIS PACIENTES ===");
        System.out.println("Email: " + email);
        
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        
        if (usuario == null) {
            System.out.println("Usuario no encontrado");
            model.addAttribute("pacientes", List.of());
            return "login/psicologo/mis-pacientes";
        }
        
        System.out.println("Usuario ID: " + usuario.getId());
        
        //  IMPORTANtisimoooooo buscar por usuario_id, NO por IDDDDD
        Especialista psicologo = especialistaRepository.findByUsuarioId(usuario.getId());
        
        if (psicologo == null) {
            System.out.println("Especialista no encontrado para usuario_id: " + usuario.getId());
            model.addAttribute("pacientes", List.of());
            return "login/psicologo/mis-pacientes";
        }
        
        System.out.println("Especialista encontrado - ID: " + psicologo.getId() + ", usuario_id: " + psicologo.getUsuario().getId());
        
        // Buscar pacientes asignados a este especialista
        List<Paciente> misPacientes = pacienteRepository.findByEspecialistaId(psicologo.getId());
        
        System.out.println("Pacientes encontrados: " + misPacientes.size());
        for (Paciente p : misPacientes) {
            System.out.println("  - Paciente: " + p.getUsuario().getNombre());
        }
        
        model.addAttribute("pacientes", misPacientes);
        return "login/psicologo/mis-pacientes";
    }
}