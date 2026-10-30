package com.example.proyecto.JPA;

import jakarta.persistence.*;

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


    public ObjetoBusqueda() {}

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
}
