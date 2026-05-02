package com.neurofocus.neurofocus.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neurofocus.neurofocus.Models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}