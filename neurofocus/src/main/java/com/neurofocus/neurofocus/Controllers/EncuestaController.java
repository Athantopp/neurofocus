package com.neurofocus.neurofocus.Controllers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class EncuestaController {
    private List<Map<String, String>> getPreguntas() {
        List<Map<String, String>> preguntas = new ArrayList<>();
        preguntas.add(Map.of("texto", "¿Te sientes abrumado por tus responsabilidades?", "tipo", "ESTRES"));
        preguntas.add(Map.of("texto", "¿Te preocupa excesivamente el futuro?", "tipo", "ANSIEDAD"));
        preguntas.add(Map.of("texto", "¿Has perdido interés en actividades que antes disfrutabas?", "tipo", "DEPRESION"));
        preguntas.add(Map.of("texto", "¿Tienes dificultades para dormir?", "tipo", "ESTRES"));
        preguntas.add(Map.of("texto", "¿Sientes nerviosismo con frecuencia?", "tipo", "ANSIEDAD"));
        preguntas.add(Map.of("texto", "¿Te sientes triste o vacío la mayor parte del tiempo?", "tipo", "DEPRESION"));
        preguntas.add(Map.of("texto", "¿Te irritas fácilmente?", "tipo", "ESTRES"));
        preguntas.add(Map.of("texto", "¿Tienes pensamientos negativos recurrentes?", "tipo", "ANSIEDAD"));
        preguntas.add(Map.of("texto", "¿Has notado cambios en tu apetito?", "tipo", "DEPRESION"));
        preguntas.add(Map.of("texto", "¿Te cuesta concentrarte?", "tipo", "ESTRES"));
        return preguntas;
    }
    
    @GetMapping("/")
    public String mostrarEncuesta(Model model) {
        model.addAttribute("preguntas", getPreguntas());
        return "login/usuario/encuesta";
    }
    
    @PostMapping("/procesar-encuesta")
    public String procesarEncuesta(@RequestParam Map<String, String> respuestas, HttpSession session, Model model) {
        int estres=0, ansiedad=0, depresion=0;
        int cEstres=0, cAnsiedad=0, cDepresion=0;
        List<Map<String, String>> preguntas = getPreguntas();
        for(int i=0; i<preguntas.size(); i++) {
            String val = respuestas.get("pregunta_"+i);
            if(val!=null) {
                int v = Integer.parseInt(val);
                switch(preguntas.get(i).get("tipo")) {
                    case "ESTRES": estres+=v; cEstres++; break;
                    case "ANSIEDAD": ansiedad+=v; cAnsiedad++; break;
                    case "DEPRESION": depresion+=v; cDepresion++; break;
                }
            }
        }
        int pEstres = (estres*100)/(cEstres*5);
        int pAnsiedad = (ansiedad*100)/(cAnsiedad*5);
        int pDepresion = (depresion*100)/(cDepresion*5);
        int max = Math.max(pEstres, Math.max(pAnsiedad, pDepresion));
        String riesgo = max<34 ? "BAJO" : (max<67 ? "MEDIO" : "ALTO");
        String causa = pEstres>=60 ? "estrés crónico" : (pAnsiedad>=60 ? "ansiedad generalizada" : (pDepresion>=60 ? "depresión" : "estrés, ansiedad o depresión leve"));
        model.addAttribute("porcentajeEstres", pEstres);
        model.addAttribute("porcentajeAnsiedad", pAnsiedad);
        model.addAttribute("porcentajeDepresion", pDepresion);
        model.addAttribute("nivelRiesgo", riesgo);
        model.addAttribute("posibleCausa", causa);
        return "login/usuario/resultado-encuesta";
    }
}