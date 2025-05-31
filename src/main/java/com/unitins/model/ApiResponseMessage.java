package com.unitins.model;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class ApiResponseMessage {
    private String message;
    private boolean success;

    public ApiResponseMessage(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public static ApiResponseMessage success(String message) {
        return new ApiResponseMessage(message, true);
    }

    public static ApiResponseMessage error(String message) {
        return new ApiResponseMessage(message, false);
    }
}