package com.example.courseworksocks.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileProcessingException extends RuntimeException {
    public FileProcessingException() {
        super("The problem during reading tne file");
    }

    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
    protected FileProcessingException (String message, Throwable cause, boolean enableSupperession, boolean writableStackTrace) {
        super(message, cause, enableSupperession, writableStackTrace);
    }
}

