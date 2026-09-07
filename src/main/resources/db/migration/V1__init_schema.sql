-- ms-campuslab-report: esquema inicial (hechos de reservas + métricas agregadas para KPIs)
-- Modelo normalizado (3FN). Motor: PostgreSQL.
-- Este servicio consume Kafka (bookings.events) y no accede a la BD de bookings.

-- Hecho normalizado por cada cambio de estado de una reserva (fuente para las agregaciones).
-- lab_id referencia catalog.labs.id (otro microservicio/BD: sin FK física).
-- event_id = envelope.eventId del mensaje Kafka; garantiza idempotencia del consumidor.
CREATE TABLE booking_event_facts (
    id          BIGSERIAL PRIMARY KEY,
    event_id    VARCHAR(64) NOT NULL,
    booking_id  BIGINT NOT NULL,
    lab_id      BIGINT NOT NULL,
    status      VARCHAR(20) NOT NULL
        CHECK (status IN ('SOLICITADA', 'APROBADA', 'EN_PREPARACION', 'EN_USO', 'DEVUELTA', 'CANCELADA')),
    occurred_at TIMESTAMPTZ NOT NULL,
    received_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_booking_event_facts_event_id UNIQUE (event_id)
);

CREATE INDEX idx_booking_event_facts_booking_id ON booking_event_facts (booking_id);
CREATE INDEX idx_booking_event_facts_lab_occurred_at ON booking_event_facts (lab_id, occurred_at);

-- Recursos (equipos/insumos) involucrados en cada hecho de reserva.
-- Tabla puente para no repetir grupos dentro de booking_event_facts.
-- resource_id referencia catalog.resources.id (otro microservicio/BD: sin FK física).
CREATE TABLE booking_event_resources (
    id                    BIGSERIAL PRIMARY KEY,
    booking_event_fact_id BIGINT NOT NULL REFERENCES booking_event_facts (id) ON DELETE CASCADE,
    resource_id           BIGINT NOT NULL,
    quantity              INTEGER NOT NULL DEFAULT 1 CHECK (quantity > 0)
);

CREATE INDEX idx_booking_event_resources_fact_id ON booking_event_resources (booking_event_fact_id);
CREATE INDEX idx_booking_event_resources_resource_id ON booking_event_resources (resource_id);

-- Métrica agregada por laboratorio/hora: reservas por hora y tiempo de ciclo promedio.
-- Grano: (lab_id, bucket_hour). Recalculada/actualizada por el proceso de agregación.
CREATE TABLE booking_hourly_metrics (
    id                BIGSERIAL PRIMARY KEY,
    lab_id            BIGINT NOT NULL,
    bucket_hour       TIMESTAMPTZ NOT NULL,
    bookings_count    INTEGER NOT NULL DEFAULT 0 CHECK (bookings_count >= 0),
    avg_cycle_minutes NUMERIC(10, 2),
    CONSTRAINT uq_booking_hourly_metrics_lab_bucket UNIQUE (lab_id, bucket_hour)
);

CREATE INDEX idx_booking_hourly_metrics_bucket_hour ON booking_hourly_metrics (bucket_hour);

-- Métrica agregada por recurso y periodo: soporta "equipos ocupados" y "top recursos".
CREATE TABLE resource_usage_metrics (
    id                     BIGSERIAL PRIMARY KEY,
    resource_id            BIGINT NOT NULL,
    period_start           TIMESTAMPTZ NOT NULL,
    period_end             TIMESTAMPTZ NOT NULL,
    usage_count            INTEGER NOT NULL DEFAULT 0 CHECK (usage_count >= 0),
    total_duration_minutes NUMERIC(12, 2) NOT NULL DEFAULT 0,
    CONSTRAINT uq_resource_usage_metrics_resource_period UNIQUE (resource_id, period_start, period_end),
    CONSTRAINT ck_resource_usage_metrics_period CHECK (period_end > period_start)
);

CREATE INDEX idx_resource_usage_metrics_resource_id ON resource_usage_metrics (resource_id);
