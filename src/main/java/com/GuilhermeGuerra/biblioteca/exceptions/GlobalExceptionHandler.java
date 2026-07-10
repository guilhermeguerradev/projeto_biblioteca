package com.GuilhermeGuerra.biblioteca.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    record ErroResponse(String mensagem, LocalDateTime timestamp, int status) {
        ErroResponse(String mensagem, int status) {
            this(mensagem, LocalDateTime.now(), status);
        }
    }

    @ExceptionHandler({
            NotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErroResponse handleNotFound(BusinessException ex) {
        return new ErroResponse(ex.getMessage(), 404);
    }

    @ExceptionHandler({
            AlreadyRegisteredException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroResponse handleConflict(BusinessException ex) {
        return new ErroResponse(ex.getMessage(), 409);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse handleBusiness(BusinessException ex) {
        return new ErroResponse(ex.getMessage(), 400);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroResponse handleValidacao(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(","));
        return new ErroResponse(mensagem,400);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErroResponse handleGenerico(Exception ex) {
        return new ErroResponse("Erro interno no servidor", 500);
    }

}



