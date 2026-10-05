# API de reportería

Todos los endpoints requieren un JWT válido con el rol `Admin`. `GET /actuator/health` es público para healthchecks. El servicio usa PostgreSQL y consume `bookings.events`; no consulta la base de datos de bookings.

## Periodos

Los endpoints de métricas y detalle aceptan un único selector de periodo:

- `range=last24h|last7d|last30d|last90d`
- `from=2026-01-01&to=2026-04-01` (el límite `to` es exclusivo)
- `semester=2026-S1|2026-S2`
- `lastSemesters=1..6`
- `month=2026-03`
- `lastMonths=N`
- `week=2026-W10`
- `lastWeeks=N`

Los límites de fecha se interpretan en `TZ` (por defecto `America/Santiago`) y el máximo se configura con `REPORT_MAX_RANGE_DAYS` (por defecto 1095 días). Un selector inválido produce `400 application/problem+json`.

## Endpoints

### `GET /api/report/kpis`

Devuelve las métricas horarias de reservas para el periodo. Ejemplo:

```http
GET /api/report/kpis?semester=2026-S1
```

```json
[
  {
    "labId": 4,
    "bucketHour": "2026-03-02T10:00:00-03:00[America/Santiago]",
    "bookingsCount": 8,
    "avgCycleMinutes": null
  }
]
```

### `GET /api/report/top-resources`

Devuelve el uso agregado de recursos ordenado por cantidad descendente:

```http
GET /api/report/top-resources?lastMonths=12
```

### `GET /api/report/usage`

Devuelve eventos de reserva paginados. Filtros disponibles: `userId`, `labId`, `status` y cualquier selector de periodo. `size` acepta entre 1 y 500. `format=json` devuelve JSON; `format=csv` devuelve CSV UTF-8 con BOM; `format=xlsx` devuelve un archivo Excel con `Content-Disposition: attachment`.

```http
GET /api/report/usage?from=2026-01-01&to=2026-02-01&labId=4&page=0&size=50
```

La respuesta es un `Page` de Spring y actualmente expone `bookingId`, identidad de usuario si llegó en Kafka, laboratorio, estado y timestamp del evento.

## Kafka

El consumidor usa el DTO local `BookingEventMessage`, ignora headers de tipo del productor y tolera campos desconocidos. Los mensajes que fallan después de los reintentos se publican en `report.DLT` (configurable mediante `KAFKA_DLT_TOPIC`). La deduplicación usa `event_id` único en PostgreSQL.

El payload disponible en este repositorio solo define de forma confirmada `bookingId`, `labId`, `status` y `resources`. `userId`, `userEmail`, `userName` y `labName` son opcionales y se guardan únicamente si llegan en el evento. No se inventan nombres ni fechas que no estén presentes en el contrato real de bookings; ese contrato debe confirmarse con el repositorio `ms-campuslab-bookings`, que no está montado en este workspace.

## Configuración mínima

```env
DB_URL=jdbc:postgresql://report-db:5432/campuslab_report_db
DB_USER=campuslab
DB_PASS=change-me
KAFKA_BOOTSTRAP_SERVERS=kafka1:9092,kafka2:9092,kafka3:9092
ISSUER_URI=https://login.microsoftonline.com/<TENANT_ID>/v2.0
TZ=America/Santiago
```
