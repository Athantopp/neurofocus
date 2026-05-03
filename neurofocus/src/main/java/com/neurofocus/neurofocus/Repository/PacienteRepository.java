package com.neurofocus.neurofocus.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.neurofocus.neurofocus.Models.Paciente;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    @Query("SELECT p FROM Paciente p WHERE p.especialistaAsignado.id = :especialistaId")
    List<Paciente> findByEspecialistaId(@Param("especialistaId") Long especialistaId);
}