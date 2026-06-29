package com.GuilhermeGuerra.biblioteca.exceptions;

public class BusinessException extends RuntimeException {

    public BusinessException(String mensagem) {
        super(mensagem);
    }
}
