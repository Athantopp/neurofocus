package com.neurofocus.neurofocus.Controllers;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.neurofocus.neurofocus.Models.Especialista;
import com.neurofocus.neurofocus.Models.Paciente;
import com.neurofocus.neurofocus.Models.Usuario;
import com.neurofocus.neurofocus.Repository.EspecialistaRepository;
import com.neurofocus.neurofocus.Repository.PacienteRepository;
import com.neurofocus.neurofocus.Repository.UsuarioRepository;

@Controller
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PacienteRepository pacienteRepository;
    private final EspecialistaRepository especialistaRepository;

    public AuthController(UsuarioRepository usuarioRepository,
                         PasswordEncoder passwordEncoder,
                         PacienteRepository pacienteRepository,
                         EspecialistaRepository especialistaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.pacienteRepository = pacienteRepository;
        this.especialistaRepository = especialistaRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login/usuario/login";
    }

    @GetMapping("/registro")
    public String registroForm() {
        return "login/usuario/registro";
    }

    @PostMapping("/registro")
    public String registrar(@RequestParam String nombre,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String rol) {

        System.out.println("=== REGISTRO ===");
        System.out.println("Rol recibido: [" + rol + "]");

        if (usuarioRepository.existsByEmail(email)) {
            System.out.println("Email ya existe");
            return "redirect:/registro?error=email_existe";
        }

        // Crear y guardar usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol.toUpperCase());
        Usuario savedUser = usuarioRepository.save(usuario);
        System.out.println("Usuario guardado ID: " + savedUser.getId());

        // Guardar en PACIENTES
        if ("PACIENTE".equalsIgnoreCase(rol)) {
            System.out.println(">>> Creando Paciente...");
            Paciente paciente = new Paciente();
            paciente.setUsuario(savedUser);
            paciente.setTelefono("No especificado");
            pacienteRepository.save(paciente);
            System.out.println("Paciente creado con ID: " + paciente.getId());
        }
        // Guardar en ESPECIALISTAS
        else if ("PSICOLOGO".equalsIgnoreCase(rol)) {
            System.out.println(">>> Creando Especialista...");
            Especialista especialista = new Especialista();
            especialista.setUsuario(savedUser);
            especialista.setEspecialidad("Por definir");
            especialista.setHorarioAtencion("Lunes a Viernes 9am-5pm");
            especialista.setTelefono("No especificado");
            especialista.setModalidad("AMBAS");
            especialista.setActivo(true);
            especialistaRepository.save(especialista);
            System.out.println("Especialista creado con ID: " + especialista.getId());
        }

        return "redirect:/login";
    }
}