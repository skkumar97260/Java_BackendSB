package com.pixalive.backend.response;

import java.util.Objects;

public class ApiResponse<T> {
    private String message;
    private T result;

    public ApiResponse() {
        // No-args constructor
    }

    public ApiResponse(String message, T result) {
        this.message = message;
        this.result = result;
    }



    // Getter for message
    public String getMessage() {
        return message;
    }

    // Setter for message
    public void setMessage(String message) {
        this.message = message;
    }

    // Getter for data
    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "message='" + message + '\'' +
                ", result=" + result +
                '}';
    }


}
