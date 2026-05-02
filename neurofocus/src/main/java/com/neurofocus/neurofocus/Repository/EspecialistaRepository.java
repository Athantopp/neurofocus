package com.neurofocus.neurofocus.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neurofocus.neurofocus.Models.Especialista;

@Repository
public interface EspecialistaRepository extends JpaRepository<Especialista, Long> {
}