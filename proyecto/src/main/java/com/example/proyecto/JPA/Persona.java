package com.example.proyecto.JPA;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Entity
public class Persona {

    @Id
    private String dni;

    private String nombre;
    private String apellido;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL)
    private List<ObjetoBusqueda> objetosBuscados;

    private LocalDateTime updatedAt;

    public Persona() {}

    @PrePersist
    @PreUpdate
    private void actualizarFechaModificacion() {
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public List<ObjetoBusqueda> getObjetosBuscados() { return objetosBuscados; }
    public void setObjetosBuscados(List<ObjetoBusqueda> objetosBuscados) { this.objetosBuscados = objetosBuscados; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
