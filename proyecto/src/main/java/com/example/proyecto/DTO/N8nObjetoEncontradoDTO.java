package com.example.proyecto.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record N8nObjetoEncontradoDTO(
        @NotBlank String externalId,
        @NotBlank String nombre,
        String color,
        String tipo,
        @Size(max = 500) String descripcion,
        @NotNull LocalDate fechaEncontrado
) {}
