package com.neurofocus.neurofocus.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.neurofocus.neurofocus.Models.Especialista;

@Repository
public interface EspecialistaRepository extends JpaRepository<Especialista, Long> {
    @Query("SELECT e FROM Especialista e WHERE e.usuario.id = :usuarioId")
    Especialista findByUsuarioId(@Param("usuarioId") Long usuarioId);

}