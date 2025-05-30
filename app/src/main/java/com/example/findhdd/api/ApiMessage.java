package com.example.findhdd.api;

import com.google.gson.annotations.SerializedName;

public class ApiMessage {
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }
}
