package com.example.proyecto.DTO;

import java.time.LocalDateTime;
import java.util.List;

public record SyncPayload(
        String batchId,
        LocalDateTime generatedAt,
        LocalDateTime from,
        LocalDateTime until,
        List<PersonaSyncDTO> personas,
        List<ObjetoBusquedaSyncDTO> objetosBusqueda
) {
    public record PersonaSyncDTO(
            String dni,
            String nombre,
            String apellido,
            LocalDateTime updatedAt
    ) {}

    public record PersonaReferenciaDTO(
            String dni,
            String nombre,
            String apellido
    ) {}

    public record ObjetoBusquedaSyncDTO(
            Long id,
            String nombre,
            String color,
            String tipo,
            LocalDateTime updatedAt,
            PersonaReferenciaDTO persona
    ) {}
}
