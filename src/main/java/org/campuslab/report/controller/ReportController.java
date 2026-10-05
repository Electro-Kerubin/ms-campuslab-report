package org.campuslab.report.controller;

import lombok.RequiredArgsConstructor;
import org.campuslab.report.dto.BookingHourlyMetricResponse;
import org.campuslab.report.dto.ResourceUsageMetricResponse;
import org.campuslab.report.dto.UsageResponse;
import org.campuslab.report.domain.repository.BookingEventFactRepository;
import org.campuslab.report.service.PeriodResolver;
import org.campuslab.report.service.ResolvedPeriod;
import org.campuslab.report.service.ReportExportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final PeriodResolver periodResolver;
    private final BookingEventFactRepository factRepository;
    private final ReportExportService exportService;

    @GetMapping("/kpis")
    public ResponseEntity<List<BookingHourlyMetricResponse>> getKpis(
            @RequestParam(required = false) String range,
            @RequestParam(required = false) String from, @RequestParam(required = false) String to,
            @RequestParam(required = false) String semester, @RequestParam(required = false) Integer lastSemesters,
            @RequestParam(required = false) String month, @RequestParam(required = false) Integer lastMonths,
            @RequestParam(required = false) String week, @RequestParam(required = false) Integer lastWeeks) {
        return ResponseEntity.ok(kpiReportService.getHourlyMetrics(resolve(
                range, from, to, semester, lastSemesters, month, lastMonths, week, lastWeeks)));
    }

    @GetMapping("/top-resources")
    public ResponseEntity<List<ResourceUsageMetricResponse>> getTopResources(
            @RequestParam(required = false) String range,
            @RequestParam(required = false) String from, @RequestParam(required = false) String to,
            @RequestParam(required = false) String semester, @RequestParam(required = false) Integer lastSemesters,
            @RequestParam(required = false) String month, @RequestParam(required = false) Integer lastMonths,
            @RequestParam(required = false) String week, @RequestParam(required = false) Integer lastWeeks) {
        return ResponseEntity.ok(kpiReportService.getTopResources(resolve(
                range, from, to, semester, lastSemesters, month, lastMonths, week, lastWeeks)));
    }

    @GetMapping("/usage")
    public ResponseEntity<?> getUsage(
            @RequestParam(required = false) String range, @RequestParam(required = false) String from,
            @RequestParam(required = false) String to, @RequestParam(required = false) String semester,
            @RequestParam(required = false) Integer lastSemesters, @RequestParam(required = false) String month,
            @RequestParam(required = false) Integer lastMonths, @RequestParam(required = false) String week,
            @RequestParam(required = false) Integer lastWeeks, @RequestParam(required = false) String userId,
            @RequestParam(required = false) Long labId, @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "json") String format,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size) {
        if (page < 0 || size < 1 || size > 500) throw new IllegalArgumentException("page/size inválidos");
        ResolvedPeriod period = resolve(range, from, to, semester, lastSemesters, month, lastMonths, week, lastWeeks);
        Page<UsageResponse> result = factRepository.search(period.from(), period.to(), userId, labId, status,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "occurredAt")))
                .map(UsageResponse::from);
        return exportService.exportUsage(result, format);
    }

    private ResolvedPeriod resolve(String range, String from, String to, String semester, Integer lastSemesters,
                                   String month, Integer lastMonths, String week, Integer lastWeeks) {
        return periodResolver.resolve(range, from, to, semester, lastSemesters, month, lastMonths, week, lastWeeks, null);
    }
}