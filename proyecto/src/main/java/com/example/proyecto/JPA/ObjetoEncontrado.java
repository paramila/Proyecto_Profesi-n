package com.example.proyecto.JPA;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class ObjetoEncontrado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador estable generado por n8n. Permite que los reintentos
     * del webhook actualicen el mismo registro en lugar de duplicarlo.
     */
    @Column(unique = true, length = 120)
    private String externalId;

    private String nombre;
    private String color;
    private String tipo;

    @Column(length = 500)
    private String descripcion;

    private LocalDate fechaEncontrado;

    public ObjetoEncontrado() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaEncontrado() { return fechaEncontrado; }
    public void setFechaEncontrado(LocalDate fechaEncontrado) { this.fechaEncontrado = fechaEncontrado; }
}
