package com.neurofocus.neurofocus.Controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.neurofocus.neurofocus.Models.Especialista;
import com.neurofocus.neurofocus.Models.Paciente;
import com.neurofocus.neurofocus.Models.Usuario;
import com.neurofocus.neurofocus.Repository.EspecialistaRepository;
import com.neurofocus.neurofocus.Repository.PacienteRepository;
import com.neurofocus.neurofocus.Repository.UsuarioRepository;
import com.neurofocus.neurofocus.Services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/paciente")
public class PacienteController {

    private final PacienteRepository pacienteRepository;
    private final EspecialistaRepository especialistaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    public PacienteController(PacienteRepository pacienteRepository,
                              EspecialistaRepository especialistaRepository,
                              UsuarioRepository usuarioRepository,
                              EmailService emailService) {
        this.pacienteRepository = pacienteRepository;
        this.especialistaRepository = especialistaRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    // Obtener el paciente actual
    private Paciente getPacienteActual(Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario != null) {
            return pacienteRepository.findById(usuario.getId()).orElse(null);
        }
        return null;
    }

    // Catálogo de psicólogos
    @GetMapping("/catalogo")
    public String catalogoPsicologos(Model model, Authentication auth) {
        model.addAttribute("psicologos", especialistaRepository.findAll());
        model.addAttribute("paciente", getPacienteActual(auth));
        return "login/usuario/catalogo-psicologos";
    }

    // Asignar psicólogo
    @PostMapping("/asignar-psicologo/{id}")
    public String asignarPsicologo(@PathVariable Long id, Authentication auth) {
        Paciente paciente = getPacienteActual(auth);
        Especialista especialista = especialistaRepository.findById(id).orElse(null);
        
        if (paciente != null && especialista != null) {
            paciente.setEspecialistaAsignado(especialista);
            pacienteRepository.save(paciente);
            System.out.println("Psicólogo asignado: " + especialista.getUsuario().getNombre());
        }
        
        return "redirect:/paciente/mi-psicologo";
    }

    // Ver mi psicólogo
    @GetMapping("/mi-psicologo")
    public String miPsicologo(Model model, Authentication auth) {
        Paciente paciente = getPacienteActual(auth);
        model.addAttribute("paciente", paciente);
        return "login/usuario/mi-psicologo";
    }

    // Botón de pánico
    @PostMapping("/panico")
    public String botonPanico(Authentication auth, HttpSession session) {
        Paciente paciente = getPacienteActual(auth);
        
        if (paciente != null && paciente.getEspecialistaAsignado() != null) {
            String emailPsicologo = paciente.getEspecialistaAsignado().getUsuario().getEmail();
            String nombrePaciente = paciente.getUsuario().getNombre();
            String emailPaciente = paciente.getUsuario().getEmail();
            
            String asunto = " ALERTA DE PÁNICO - " + nombrePaciente;
            String contenido = """
                ALERTA DE PÁNICO 
                
                Tu paciente %s (%s) ha solicitado ayuda inmediata.
                
                Por favor, contacta a tu paciente lo antes posible.
                
                Este es un mensaje automático de la plataforma NeuroFocus.
                
                Si es una emergencia, llama al 911.
                """.formatted(nombrePaciente, emailPaciente);
            
            emailService.enviarCorreo(emailPsicologo, asunto, contenido);
            System.out.println("Alerta de pánico enviada a: " + emailPsicologo);
            session.setAttribute("mensaje", "Alerta enviada a tu psicólogo");
        } else {
            session.setAttribute("error", "No tienes un psicólogo asignado");
        }
        
        return "redirect:/paciente/mi-psicologo";
    }
}