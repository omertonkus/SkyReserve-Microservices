package com.omer.skyreserve.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor // Tüm alanlar için constructor oluşturur
public class ErrorResponse {

    private final String message; // final yaparak değiştirilemez kılıyoruz
    private final LocalDateTime timestamp = LocalDateTime.now();

}
