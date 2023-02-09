package com.example.courseworksocks.controllers;

import com.example.courseworksocks.exception.FileProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ControllerAdvisor extends ResponseEntityExceptionHandler {
     protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
    HttpHeaders headers, HttpStatus status, WebRequest request){
         Map<String, String> errors = new HashMap<>();
         ex.getBindingResult().getAllErrors().forEach((error)->{
             String fieldName = ((FieldError)error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
         });
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
     }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FileProcessingException.class)
    public String handleFileProcessingExceptions (FileProcessingException ex) {return  ex.getMessage();}
}
