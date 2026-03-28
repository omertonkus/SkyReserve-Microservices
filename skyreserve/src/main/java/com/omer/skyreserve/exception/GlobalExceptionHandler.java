package com.omer.skyreserve.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // Tum controller'ları gözetleyen merkez
public class GlobalExceptionHandler {

    // Bizim fırlattığımız özel hataları yakala
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex){
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Beklenmedik diğer tüm sistem hatalarını yakala (Güvenlik ağı)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex){
        ErrorResponse error = new ErrorResponse("A system error occurred: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);

    }

}
