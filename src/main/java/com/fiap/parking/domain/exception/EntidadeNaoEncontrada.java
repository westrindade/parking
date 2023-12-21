package com.fiap.parking.domain.exception;

public class EntidadeNaoEncontrada extends RuntimeException{
    
    public EntidadeNaoEncontrada(final String mensagem){
        super(mensagem);
    }
}
