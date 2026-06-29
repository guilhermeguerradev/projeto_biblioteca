package com.GuilhermeGuerra.biblioteca.exceptions;

public class UsernameNotFoundException extends BusinessException{

    public UsernameNotFoundException(String mensagem) {
        super(mensagem);
    }
}
