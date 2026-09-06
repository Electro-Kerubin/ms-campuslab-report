package org.campuslab.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Report Service Application
 *
 * Punto de entrada para el microservicio de reportería y analítica.
 *
 * Responsabilidades:
 * - Consumir eventos de Kafka (bookings.events)
 * - Agregar datos para KPIs en tiempo real (reservas por hora, tiempo de ciclo, etc.)
 * - Persistir agregaciones en PostgreSQL
 * - Exponer endpoints de lectura para dashboard administrativo
 * - Calcular estadísticas de ocupación y recursos más usados
 */
@SpringBootApplication
@ComponentScan(basePackages = "org.campuslab.report")
public class ReportServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportServiceApplication.class, args);
    }

}
