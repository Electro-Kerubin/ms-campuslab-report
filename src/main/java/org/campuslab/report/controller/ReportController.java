package org.campuslab.report.controller;

import lombok.RequiredArgsConstructor;
import org.campuslab.report.dto.BookingHourlyMetricResponse;
import org.campuslab.report.dto.ResourceUsageMetricResponse;
import org.campuslab.report.service.KpiReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {
    private final KpiReportService kpiReportService;

    @GetMapping("/kpis")
    public ResponseEntity<List<BookingHourlyMetricResponse>> getKpis(
            @RequestParam(defaultValue = "last24h") String range) {
        if (!"last24h".equals(range)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(kpiReportService.getHourlyMetrics(24));
    }

    @GetMapping("/top-resources")
    public ResponseEntity<List<ResourceUsageMetricResponse>> getTopResources(
            @RequestParam(defaultValue = "last7d") String range) {
        if (!"last7d".equals(range)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(kpiReportService.getTopResources(7));
    }
}