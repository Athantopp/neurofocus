package com.neurofocus.neurofocus.Models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pacientes")
public class Paciente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", unique = true)
    private Usuario usuario;
    
    private String telefono;
    
    @ManyToOne
    @JoinColumn(name = "especialista_id")
    private Especialista especialistaAsignado;
    
    // Constructores
    public Paciente() {}
    
    public Paciente(Usuario usuario) {
        this.usuario = usuario;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public Especialista getEspecialistaAsignado() { return especialistaAsignado; }
    public void setEspecialistaAsignado(Especialista especialistaAsignado) { 
        this.especialistaAsignado = especialistaAsignado; 
    }
}