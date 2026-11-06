# Proyecto Profesion - Spring Boot + MySQL + n8n

## Funcionamiento

- Cada día a las **02:00 (Europe/Madrid)**, Spring Boot consulta los registros de `ObjetoBusqueda` y `Persona` modificados desde la última sincronización correcta.
- Envía a n8n un webhook con esos datos.
- n8n procesa la información y llama al endpoint de entrada de Spring Boot para devolver objetos encontrados.
- Spring Boot guarda o actualiza esos objetos en MySQL.
- Si el webhook de salida falla, el cursor no avanza y los datos se vuelven a enviar en la siguiente ejecución.
- Los resultados de n8n se identifican por `externalId`; los reintentos actualizan el registro en lugar de duplicarlo.

## Arrancar MySQL

Desde esta carpeta:

    docker compose up -d mysql

La base de datos por defecto se llama `proyecto_profesion`. Cambia las credenciales con `DB_USERNAME`, `DB_PASSWORD` y `DB_ROOT_PASSWORD`.

## Configurar y arrancar Spring Boot

Se necesita Java 17 o superior y Maven. Configura:

- `N8N_WEBHOOK_URL`: URL del nodo Webhook de n8n que recibe el envío diario.
- `N8N_WEBHOOK_SECRET`: secreto que Spring Boot exigirá a n8n cuando llame al endpoint de entrada.
- `N8N_OUTBOUND_SECRET`: opcional; Spring Boot lo envía a n8n mediante `X-Sync-Secret`.

Las variables MySQL son `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`.

Ejemplo:

    export N8N_WEBHOOK_URL="https://TU_N8N/webhook/sincronizar-cambios"
    export N8N_WEBHOOK_SECRET="un-secreto-largo-y-aleatorio"
    export N8N_OUTBOUND_SECRET="otro-secreto-opcional"
    mvn spring-boot:run

También puedes empaquetar y arrancar:

    mvn clean package
    java -jar target/proyecto-0.0.1-SNAPSHOT.jar

Hibernate actualiza el esquema con `ddl-auto=update` para facilitar el desarrollo. En producción utiliza Flyway o Liquibase.

## Webhook de salida: Spring Boot -> n8n

Spring Boot envía un POST a `N8N_WEBHOOK_URL` con esta estructura:

    {
      "batchId": "uuid-de-la-ejecucion",
      "generatedAt": "2026-11-06T01:00:00",
      "from": "2026-11-05T01:00:00",
      "until": "2026-11-06T01:00:00",
      "personas": [
        {
          "dni": "12345678A",
          "nombre": "Ana",
          "apellido": "Perez",
          "updatedAt": "2026-11-05T18:30:00"
        }
      ],
      "objetosBusqueda": [
        {
          "id": 12,
          "nombre": "Mochila",
          "color": "Azul",
          "tipo": "Accesorio",
          "updatedAt": "2026-11-05T18:30:00",
          "persona": {
            "dni": "12345678A",
            "nombre": "Ana",
            "apellido": "Perez"
          }
        }
      ]
    }

En la primera ejecución se envían todos los registros. Las siguientes incluyen los que tengan `updatedAt` posterior al último envío correcto. También se llama a n8n cuando no hay cambios, con listas vacías.

## Webhook de entrada: n8n -> Spring Boot

Crea en n8n un nodo **HTTP Request** que haga POST a:

    https://TU_BACKEND/api/n8n/objetos-encontrados

Incluye la cabecera:

    X-N8N-Secret: el mismo valor configurado en N8N_WEBHOOK_SECRET

El cuerpo JSON esperado:

    {
      "batchId": "uuid-de-la-ejecucion-recibida",
      "objetosEncontrados": [
        {
          "externalId": "identificador-unico-del-resultado-en-n8n",
          "nombre": "Mochila",
          "color": "Azul",
          "tipo": "Accesorio",
          "descripcion": "Mochila encontrada en recepción",
          "fechaEncontrado": "2026-11-06"
        }
      ]
    }

Obligatorios: `batchId`, `objetosEncontrados`, `externalId`, `nombre` y `fechaEncontrado`. La fecha usa formato ISO `yyyy-MM-dd`. La lista puede estar vacía.

Respuesta correcta:

    {
      "batchId": "uuid-de-la-ejecucion-recibida",
      "totalRecibidos": 1,
      "totalGuardados": 1
    }

Usa un `externalId` estable para cada resultado. Si n8n reintenta el envío, Spring Boot actualizará el mismo registro y no lo duplicará.

## Horario

- `SYNC_CRON`: expresión cron de seis campos de Spring. Por defecto `0 0 2 * * *`.
- `SYNC_ZONE`: zona horaria. Por defecto `Europe/Madrid`.

## Detección de cambios

Los campos `updatedAt` se actualizan automáticamente cuando `ObjetoBusqueda` y `Persona` se guardan o modifican mediante JPA/Spring Data. Las escrituras SQL directas de otras aplicaciones no ejecutan los callbacks JPA; para detectarlas habría que añadir triggers MySQL o CDC.

## Seguridad

El webhook entrante requiere el secreto `X-N8N-Secret`. En producción utiliza HTTPS, secretos largos almacenados fuera del repositorio y credenciales MySQL específicas. El payload de personas contiene DNI; envía esos datos a n8n únicamente si tu flujo necesita procesarlos.
