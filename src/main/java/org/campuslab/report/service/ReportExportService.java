package org.campuslab.report.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.campuslab.report.dto.UsageResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class ReportExportService {
    public ResponseEntity<?> exportUsage(Page<UsageResponse> page, String format) {
        return switch (format.toLowerCase()) {
            case "json" -> ResponseEntity.ok(page);
            case "csv" -> download(csv(page), "usage.csv", "text/csv; charset=UTF-8");
            case "xlsx" -> download(xlsx(page), "usage.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            default -> throw new IllegalArgumentException("format debe ser json, csv o xlsx");
        };
    }

    private String csv(Page<UsageResponse> page) {
        StringBuilder csv = new StringBuilder("\uFEFFbookingId,userId,userEmail,userName,labId,labName,status,occurredAt\n");
        page.getContent().forEach(item -> csv.append(value(item.bookingId())).append(',')
                .append(value(item.userId())).append(',').append(value(item.userEmail())).append(',')
                .append(value(item.userName())).append(',').append(value(item.labId())).append(',')
                .append(value(item.labName())).append(',').append(value(item.status())).append(',')
                .append(value(item.occurredAt())).append('\n'));
        return csv.toString();
    }

    private byte[] xlsx(Page<UsageResponse> page) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("usage");
            Row header = sheet.createRow(0);
            String[] columns = {"bookingId", "userId", "userEmail", "userName", "labId", "labName", "status", "occurredAt"};
            for (int index = 0; index < columns.length; index++) header.createCell(index).setCellValue(columns[index]);
            int rowIndex = 1;
            for (UsageResponse item : page.getContent()) {
                Row row = sheet.createRow(rowIndex++);
                Object[] values = {item.bookingId(), item.userId(), item.userEmail(), item.userName(), item.labId(),
                        item.labName(), item.status(), value(item.occurredAt())};
                for (int index = 0; index < values.length; index++) row.createCell(index).setCellValue(value(values[index]));
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo generar el archivo XLSX", exception);
        }
    }

    private ResponseEntity<?> download(Object body, String filename, String contentType) {
        byte[] bytes = body instanceof String text ? text.getBytes(StandardCharsets.UTF_8) : (byte[]) body;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    private String value(Object value) {
        if (value == null) return "";
        String text = String.valueOf(value);
        return '"' + text.replace("\"", "\"\"") + '"';
    }
}