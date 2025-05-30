package com.example.findhdd.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class UserDTO {
    @SerializedName("id")
    private Long id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role;

    public boolean isAdmin() {
        return "ROLE_ADMIN".equals(role);
    }
}