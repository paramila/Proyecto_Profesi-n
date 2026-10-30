package com.example.proyecto.repositorios;

import com.example.proyecto.JPA.ObjetoEncontrado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjetoEncontrado_repository extends JpaRepository<ObjetoEncontrado,String> {
}
