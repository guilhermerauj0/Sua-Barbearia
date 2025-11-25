package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.ApiErrorDto;
import com.barbearia.domain.exceptions.AcessoNegadoException;
import com.barbearia.domain.exceptions.AgendamentoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDto> handleIllegalArgumentException(IllegalArgumentException ex,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Dados Inválidos", ex.getMessage(), request);
    }

    @ExceptionHandler(AgendamentoNaoEncontradoException.class)
    public ResponseEntity<ApiErrorDto> handleAgendamentoNaoEncontradoException(AgendamentoNaoEncontradoException ex,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Não Encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler({ AcessoNegadoException.class, AccessDeniedException.class })
    public ResponseEntity<ApiErrorDto> handleAcessoNegadoException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "Acesso Negado", ex.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorDto> handleBadCredentialsException(BadCredentialsException ex,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Não Autorizado", "Credenciais inválidas", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleValidationExceptions(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce("", (acc, curr) -> acc.isEmpty() ? curr : acc + "; " + curr);

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Erro de Validação", errorMessage, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGenericException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro Interno",
                "Ocorreu um erro inesperado: " + ex.getMessage(), request);
    }

    private ResponseEntity<ApiErrorDto> buildErrorResponse(HttpStatus status, String error, String message,
            HttpServletRequest request) {
        ApiErrorDto apiError = ApiErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(apiError);
    }
}
