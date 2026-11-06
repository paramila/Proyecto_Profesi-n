package com.example.proyecto.servicios;

import com.example.proyecto.DTO.SyncPayload;
import com.example.proyecto.JPA.ObjetoBusqueda;
import com.example.proyecto.JPA.Persona;
import com.example.proyecto.JPA.SyncState;
import com.example.proyecto.repositorios.ObjetoBusqueda_repository;
import com.example.proyecto.repositorios.SyncState_repository;
import com.example.proyecto.repositorios.Usuario_repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SincronizacionN8nService {

    private static final Logger log = LoggerFactory.getLogger(SincronizacionN8nService.class);
    private static final String SYNC_STATE_ID = "n8n-objetos-y-personas";

    private final ObjetoBusqueda_repository objetoBusquedaRepository;
    private final Usuario_repository personaRepository;
    private final SyncState_repository syncStateRepository;
    private final RestClient restClient;
    private final Clock clock;

    @Value("${n8n.webhook.url:}")
    private String webhookUrl;

    @Value("${n8n.webhook.outbound-secret:}")
    private String outboundSecret;

    public SincronizacionN8nService(
            ObjetoBusqueda_repository objetoBusquedaRepository,
            Usuario_repository personaRepository,
            SyncState_repository syncStateRepository,
            RestClient restClient,
            Clock clock) {
        this.objetoBusquedaRepository = objetoBusquedaRepository;
        this.personaRepository = personaRepository;
        this.syncStateRepository = syncStateRepository;
        this.restClient = restClient;
        this.clock = clock;
    }

    /**
     * Ejecuta cada día a las 02:00 (Europe/Madrid por defecto).
     * Si n8n falla, el cursor no avanza y los cambios se reenvían.
     */
    @Scheduled(
            cron = "${app.sync.cron:0 0 2 * * *}",
            zone = "${app.sync.zone:Europe/Madrid}"
    )
    public void ejecutarSincronizacionDiaria() {
        try {
            sincronizarCambios();
        } catch (Exception ex) {
            log.error("No se pudo completar la sincronización diaria con n8n", ex);
        }
    }

    public void sincronizarCambios() {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            throw new IllegalStateException("Configura N8N_WEBHOOK_URL antes de ejecutar la sincronización");
        }

        // Fijamos el límite superior al inicio: los cambios concurrentes
        // durante el envío quedan pendientes para el siguiente ciclo.
        LocalDateTime hasta = LocalDateTime.now(clock).withNano(0);
        SyncState estado = syncStateRepository.findById(SYNC_STATE_ID)
                .orElseGet(() -> new SyncState(SYNC_STATE_ID, null));
        LocalDateTime desde = estado.getLastSuccessfulSyncAt();

        List<ObjetoBusqueda> objetos = desde == null
                ? objetoBusquedaRepository.findAll()
                : objetoBusquedaRepository.findByUpdatedAtAfterAndUpdatedAtLessThanEqual(desde, hasta);

        List<Persona> personas = desde == null
                ? personaRepository.findAll()
                : personaRepository.findByUpdatedAtAfterAndUpdatedAtLessThanEqual(desde, hasta);

        List<SyncPayload.PersonaSyncDTO> personasDTO = personas.stream()
                .map(persona -> new SyncPayload.PersonaSyncDTO(
                        persona.getDni(),
                        persona.getNombre(),
                        persona.getApellido(),
                        persona.getUpdatedAt()
                ))
                .toList();

        List<SyncPayload.ObjetoBusquedaSyncDTO> objetosDTO = objetos.stream()
                .map(objeto -> {
                    Persona persona = objeto.getPersona();
                    SyncPayload.PersonaReferenciaDTO referencia = persona == null
                            ? null
                            : new SyncPayload.PersonaReferenciaDTO(
                                    persona.getDni(),
                                    persona.getNombre(),
                                    persona.getApellido()
                            );

                    return new SyncPayload.ObjetoBusquedaSyncDTO(
                            objeto.getId(),
                            objeto.getNombre(),
                            objeto.getColor(),
                            objeto.getTipo(),
                            objeto.getUpdatedAt(),
                            referencia
                    );
                })
                .toList();

        SyncPayload payload = new SyncPayload(
                UUID.randomUUID().toString(),
                LocalDateTime.now(clock),
                desde,
                hasta,
                personasDTO,
                objetosDTO
        );

        RestClient.RequestBodySpec request = restClient.post().uri(webhookUrl);

        if (outboundSecret != null && !outboundSecret.isBlank()) {
            request.header("X-Sync-Secret", outboundSecret);
        }

        // Un error HTTP impide avanzar el cursor.
        request.body(payload).retrieve().toBodilessEntity();

        estado.setLastSuccessfulSyncAt(hasta);
        syncStateRepository.save(estado);

        log.info(
                "Sincronización enviada a n8n. batchId={}, personas={}, objetosBusqueda={}, desde={}, hasta={}",
                payload.batchId(), personasDTO.size(), objetosDTO.size(), desde, hasta
        );
    }
}
