package com.example.proyecto.repositorios;

import com.example.proyecto.JPA.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Usuario_repository extends JpaRepository<Persona,String> {
}
