package com.fiap.parking.domain.exception;

import com.fiap.parking.infra.utils.Utils;

public class EntidadeNaoEncontrada extends RuntimeException{
    
    public EntidadeNaoEncontrada(final String mensagem){
        super(mensagem.contains(".") ?  Utils.getMessage(mensagem): mensagem);
    }
}
