package com.fiap.parking.domain.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValitationExcepetion(MethodArgumentNotValidException exception){
        List<String> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField()
                + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

//    @ExceptionHandler(TransactionSystemException.class)
//    public ResponseEntity<List<String>> handleTransactionException(TransactionSystemException exception){
//        System.out.println("Entrei");
//        Throwable rootCause = exception.getRootCause();
//
//        if (rootCause instanceof ConstraintViolationException) {
//            ConstraintViolationException constraintViolationException = (ConstraintViolationException) rootCause;
//
//            List<String> errors = constraintViolationException.getConstraintViolations().stream()
//                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
//                    .collect(Collectors.toList());
//
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
//        }
//
//        // Se não for uma ConstraintViolationException, trata de outra forma ou retorna um erro genérico
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonList("Erro na transação JPA"));
//    }
}
