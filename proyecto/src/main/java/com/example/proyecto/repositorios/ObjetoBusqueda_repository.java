package com.example.proyecto.repositorios;

import com.example.proyecto.JPA.ObjetoBusqueda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ObjetoBusqueda_repository extends JpaRepository<ObjetoBusqueda, Long> {

    List<ObjetoBusqueda> findByUpdatedAtAfterAndUpdatedAtLessThanEqual(
            LocalDateTime desde, LocalDateTime hasta);
}
