package org.campuslab.report.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReportExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleInvalidPeriod(IllegalArgumentException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setTitle("Parámetros de periodo inválidos");
        return problem;
    }
}