package com.neurofocus.neurofocus.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neurofocus.neurofocus.Models.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}