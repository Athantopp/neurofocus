package com.neurofocus.neurofocus.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.neurofocus.neurofocus.Models.Cita;
import com.neurofocus.neurofocus.Models.Especialista;
import com.neurofocus.neurofocus.Models.Paciente;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    
    List<Cita> findByPacienteOrderByFechaHoraDesc(Paciente paciente);
    
    List<Cita> findByEspecialistaOrderByFechaHoraDesc(Especialista especialista);
    
    @Query("SELECT COUNT(c) > 0 FROM Cita c WHERE c.especialista.id = :especialistaId " +
           "AND c.estado != 'CANCELADA' " +
           "AND c.fechaHora BETWEEN :inicio AND :fin")
    boolean existeConflictoHorario(@Param("especialistaId") Long especialistaId,
                                   @Param("inicio") LocalDateTime inicio,
                                   @Param("fin") LocalDateTime fin);
}