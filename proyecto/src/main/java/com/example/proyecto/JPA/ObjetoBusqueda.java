package com.example.proyecto.JPA;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
public class ObjetoBusqueda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String color;
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "persona_dni")
    private Persona persona;

    /**
     * Marca la última modificación realizada a través de JPA.
     * Se almacena en UTC para que el cursor de sincronización sea consistente.
     */
    private LocalDateTime updatedAt;

    public ObjetoBusqueda() {}

    @PrePersist
    @PreUpdate
    private void actualizarFechaModificacion() {
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
