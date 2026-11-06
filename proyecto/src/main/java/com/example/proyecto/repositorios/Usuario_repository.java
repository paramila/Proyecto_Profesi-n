package com.example.proyecto.repositorios;

import com.example.proyecto.JPA.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface Usuario_repository extends JpaRepository<Persona, String> {

    List<Persona> findByUpdatedAtAfterAndUpdatedAtLessThanEqual(
            LocalDateTime desde, LocalDateTime hasta);
}
