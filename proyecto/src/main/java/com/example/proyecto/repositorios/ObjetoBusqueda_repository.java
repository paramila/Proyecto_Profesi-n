package com.example.proyecto.repositorios;

import com.example.proyecto.JPA.ObjetoBusqueda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjetoBusqueda_repository extends JpaRepository<ObjetoBusqueda,String> {
}
