package com.neurofocus.neurofocus.Controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.neurofocus.neurofocus.Models.Cita;
import com.neurofocus.neurofocus.Models.Especialista;
import com.neurofocus.neurofocus.Models.Paciente;
import com.neurofocus.neurofocus.Models.Usuario;
import com.neurofocus.neurofocus.Repository.CitaRepository;
import com.neurofocus.neurofocus.Repository.EspecialistaRepository;
import com.neurofocus.neurofocus.Repository.PacienteRepository;
import com.neurofocus.neurofocus.Repository.UsuarioRepository;

@Controller
@RequestMapping("/cita")
public class CitaController {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final EspecialistaRepository especialistaRepository;
    private final UsuarioRepository usuarioRepository;

    public CitaController(CitaRepository citaRepository,
                         PacienteRepository pacienteRepository,
                         EspecialistaRepository especialistaRepository,
                         UsuarioRepository usuarioRepository) {
        this.citaRepository = citaRepository;
        this.pacienteRepository = pacienteRepository;
        this.especialistaRepository = especialistaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Paciente getPacienteActual(Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario != null) {
            return pacienteRepository.findById(usuario.getId()).orElse(null);
        }
        return null;
    }

    private Especialista getPsicologoActual(Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario != null) {
            return especialistaRepository.findByUsuarioId(usuario.getId());
        }
        return null;
    }

    @GetMapping("/agendar")
    public String mostrarFormularioAgendar(Authentication auth, Model model) {
        Paciente paciente = getPacienteActual(auth);
        
        if (paciente == null || paciente.getEspecialistaAsignado() == null) {
            return "redirect:/paciente/mi-psicologo?sinPsicologo";
        }
        
        model.addAttribute("psicologo", paciente.getEspecialistaAsignado());
        model.addAttribute("cita", new Cita());
        return "login/usuario/agendar-cita";
    }

    @PostMapping("/agendar")
    public String agendarCita(@RequestParam String fecha,
                             @RequestParam String hora,
                             @RequestParam String modalidad,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {
        
        Paciente paciente = getPacienteActual(auth);
        
        if (paciente == null || paciente.getEspecialistaAsignado() == null) {
            return "redirect:/paciente/mi-psicologo";
        }
        
        LocalDateTime fechaHora = LocalDateTime.parse(fecha + "T" + hora);
        
        if (fechaHora.isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "No se pueden agendar citas en el pasado");
            return "redirect:/cita/agendar";
        }
        
        boolean conflicto = citaRepository.existeConflictoHorario(
            paciente.getEspecialistaAsignado().getId(),
            fechaHora.minusMinutes(30),
            fechaHora.plusMinutes(30)
        );
        
        if (conflicto) {
            redirectAttributes.addFlashAttribute("error", "El psicólogo ya tiene una cita en ese horario");
            return "redirect:/cita/agendar";
        }
        
        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setEspecialista(paciente.getEspecialistaAsignado());
        cita.setFechaHora(fechaHora);
        cita.setModalidad(modalidad);
        
        citaRepository.save(cita);
        
        redirectAttributes.addFlashAttribute("mensaje", "Cita agendada exitosamente");
        return "redirect:/cita/mis-citas";
    }

    @GetMapping("/mis-citas")
    public String misCitas(Authentication auth, Model model) {
        Paciente paciente = getPacienteActual(auth);
        
        if (paciente != null) {
            List<Cita> citas = citaRepository.findByPacienteOrderByFechaHoraDesc(paciente);
            model.addAttribute("citas", citas);
            model.addAttribute("paciente", paciente);
        }
        
        return "login/usuario/mis-citas";
    }

    @PostMapping("/cancelar/{id}")
    public String cancelarCita(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttributes) {
        Paciente paciente = getPacienteActual(auth);
        Cita cita = citaRepository.findById(id).orElse(null);
        
        if (cita != null && paciente != null && cita.getPaciente().getId().equals(paciente.getId())) {
            if (cita.getFechaHora().minusHours(24).isBefore(LocalDateTime.now())) {
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede cancelar una cita con menos de 24 horas de anticipación");
            } else {
                cita.setEstado("CANCELADA");
                citaRepository.save(cita);
                redirectAttributes.addFlashAttribute("mensaje", "Cita cancelada exitosamente");
            }
        }
        
        return "redirect:/cita/mis-citas";
    }

    @GetMapping("/psicologo/citas")
    public String citasPsicologo(Authentication auth, Model model) {
        Especialista psicologo = getPsicologoActual(auth);
        
        if (psicologo != null) {
            List<Cita> citas = citaRepository.findByEspecialistaOrderByFechaHoraDesc(psicologo);
            model.addAttribute("citas", citas);
            model.addAttribute("psicologo", psicologo);
        }
        
        return "login/psicologo/citas-pendientes";
    }
}