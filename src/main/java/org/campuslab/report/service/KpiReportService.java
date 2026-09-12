package org.campuslab.report.service;

import lombok.RequiredArgsConstructor;
import org.campuslab.report.dto.BookingEventPayload;
import org.campuslab.report.dto.BookingResourcePayload;
import org.campuslab.report.dto.BookingHourlyMetricResponse;
import org.campuslab.report.dto.ResourceUsageMetricResponse;
import org.campuslab.report.domain.model.BookingEventFact;
import org.campuslab.report.domain.model.BookingEventResource;
import org.campuslab.report.domain.model.BookingHourlyMetric;
import org.campuslab.report.domain.model.ResourceUsageMetric;
import org.campuslab.report.domain.repository.BookingEventFactRepository;
import org.campuslab.report.domain.repository.BookingHourlyMetricRepository;
import org.campuslab.report.domain.repository.ResourceUsageMetricRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KpiReportService {
    private final BookingEventFactRepository factRepository;
    private final BookingHourlyMetricRepository hourlyMetricRepository;
    private final ResourceUsageMetricRepository resourceMetricRepository;

    @Transactional
    public void processBookingEvent(String eventId, ZonedDateTime occurredAt, BookingEventPayload payload) {
        if (factRepository.existsByEventId(eventId)) {
            return;
        }

        BookingEventFact fact = new BookingEventFact();
        fact.setEventId(eventId);
        fact.setBookingId(payload.bookingId());
        fact.setLabId(payload.labId());
        fact.setStatus(payload.status());
        fact.setOccurredAt(occurredAt);
        fact.setReceivedAt(ZonedDateTime.now());

        List<BookingResourcePayload> resources = payload.resources() == null ? List.of() : payload.resources();
        resources.forEach(resourcePayload -> {
            BookingEventResource resource = new BookingEventResource();
            resource.setResourceId(resourcePayload.resourceId());
            resource.setQuantity(resourcePayload.effectiveQuantity());
            fact.addResource(resource);
        });
        factRepository.save(fact);

        if ("SOLICITADA".equals(payload.status())) {
            updateHourlyMetric(payload.labId(), occurredAt);
        }
        resources.forEach(resource -> updateResourceMetric(resource, occurredAt));
    }

    private void updateHourlyMetric(Long labId, ZonedDateTime occurredAt) {
        ZonedDateTime bucketHour = occurredAt.truncatedTo(ChronoUnit.HOURS);
        BookingHourlyMetric metric = hourlyMetricRepository.findByLabIdAndBucketHour(labId, bucketHour)
                .orElseGet(() -> {
                    BookingHourlyMetric created = new BookingHourlyMetric();
                    created.setLabId(labId);
                    created.setBucketHour(bucketHour);
                    return created;
                });
        metric.setBookingsCount(metric.getBookingsCount() + 1);
        hourlyMetricRepository.save(metric);
    }

    private void updateResourceMetric(BookingResourcePayload resource, ZonedDateTime occurredAt) {
        ZonedDateTime periodStart = occurredAt.truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime periodEnd = periodStart.plusDays(1);
        ResourceUsageMetric metric = resourceMetricRepository
                .findByResourceIdAndPeriodStartAndPeriodEnd(resource.resourceId(), periodStart, periodEnd)
                .orElseGet(() -> {
                    ResourceUsageMetric created = new ResourceUsageMetric();
                    created.setResourceId(resource.resourceId());
                    created.setPeriodStart(periodStart);
                    created.setPeriodEnd(periodEnd);
                    return created;
                });
        metric.setUsageCount(metric.getUsageCount() + resource.effectiveQuantity());
        if (metric.getTotalDurationMinutes() == null) {
            metric.setTotalDurationMinutes(BigDecimal.ZERO);
        }
        resourceMetricRepository.save(metric);
    }

    @Transactional(readOnly = true)
    public List<BookingHourlyMetricResponse> getHourlyMetrics(int hours) {
        ZonedDateTime since = ZonedDateTime.now().minusHours(hours);
        return hourlyMetricRepository.findByBucketHourAfter(since).stream()
                .sorted(java.util.Comparator.comparing(BookingHourlyMetric::getBucketHour))
                .map(BookingHourlyMetricResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ResourceUsageMetricResponse> getTopResources(int days) {
        ZonedDateTime since = ZonedDateTime.now().minusDays(days);
        return resourceMetricRepository.findByPeriodStartAfter(since).stream()
                .sorted(java.util.Comparator.comparing(ResourceUsageMetric::getUsageCount).reversed())
                .map(ResourceUsageMetricResponse::from).toList();
    }
}