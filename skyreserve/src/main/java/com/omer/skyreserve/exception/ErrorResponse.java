package com.omer.skyreserve.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private final String message;
    private final LocalDateTime timestamp = LocalDateTime.now();

}
