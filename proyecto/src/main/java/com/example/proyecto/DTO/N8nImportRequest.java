package com.example.proyecto.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record N8nImportRequest(
        @NotBlank String batchId,
        @NotNull List<@Valid N8nObjetoEncontradoDTO> objetosEncontrados
) {}
