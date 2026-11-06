package com.example.proyecto.controladores;

import com.example.proyecto.DTO.N8nImportRequest;
import com.example.proyecto.DTO.N8nImportResponse;
import com.example.proyecto.servicios.ImportacionN8nService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@RestController
@RequestMapping("/api/n8n")
public class N8nWebhookController {

    private final ImportacionN8nService importacionN8nService;

    @Value("${n8n.webhook.inbound-secret:}")
    private String inboundSecret;

    public N8nWebhookController(ImportacionN8nService importacionN8nService) {
        this.importacionN8nService = importacionN8nService;
    }

    @PostMapping("/objetos-encontrados")
    public ResponseEntity<?> recibirResultados(
            @RequestHeader(name = "X-N8N-Secret", required = false) String secret,
            @Valid @RequestBody N8nImportRequest request) {

        if (inboundSecret == null || inboundSecret.isBlank()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "El secreto del webhook no está configurado"));
        }

        byte[] esperado = inboundSecret.getBytes(StandardCharsets.UTF_8);
        byte[] recibido = secret == null
                ? new byte[0]
                : secret.getBytes(StandardCharsets.UTF_8);

        if (!MessageDigest.isEqual(esperado, recibido)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Secreto de webhook no válido"));
        }

        int guardados = importacionN8nService.guardarResultados(request);
        return ResponseEntity.ok(new N8nImportResponse(
                request.batchId(),
                request.objetosEncontrados().size(),
                guardados
        ));
    }
}
