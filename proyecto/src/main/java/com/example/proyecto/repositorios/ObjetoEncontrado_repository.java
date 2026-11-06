package com.example.proyecto.repositorios;

import com.example.proyecto.JPA.ObjetoEncontrado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ObjetoEncontrado_repository extends JpaRepository<ObjetoEncontrado, Long> {

    Optional<ObjetoEncontrado> findByExternalId(String externalId);
}
