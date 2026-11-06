package com.example.proyecto.servicios;

import com.example.proyecto.DTO.N8nImportRequest;
import com.example.proyecto.DTO.N8nObjetoEncontradoDTO;
import com.example.proyecto.JPA.ObjetoEncontrado;
import com.example.proyecto.repositorios.ObjetoEncontrado_repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImportacionN8nService {

    private final ObjetoEncontrado_repository objetoEncontradoRepository;

    public ImportacionN8nService(ObjetoEncontrado_repository objetoEncontradoRepository) {
        this.objetoEncontradoRepository = objetoEncontradoRepository;
    }

    @Transactional
    public int guardarResultados(N8nImportRequest request) {
        List<ObjetoEncontrado> entidades = new ArrayList<>();

        for (N8nObjetoEncontradoDTO dto : request.objetosEncontrados()) {
            ObjetoEncontrado entidad = objetoEncontradoRepository
                    .findByExternalId(dto.externalId())
                    .orElseGet(ObjetoEncontrado::new);

            entidad.setExternalId(dto.externalId());
            entidad.setNombre(dto.nombre());
            entidad.setColor(dto.color());
            entidad.setTipo(dto.tipo());
            entidad.setDescripcion(dto.descripcion());
            entidad.setFechaEncontrado(dto.fechaEncontrado());

            entidades.add(entidad);
        }

        objetoEncontradoRepository.saveAll(entidades);
        return entidades.size();
    }
}
