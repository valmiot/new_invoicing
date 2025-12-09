package com.tsh.erechnung.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class UserDto {
    @Data
    public static class Register {
        @Email
        @NotBlank
        private String email;
        @NotBlank
        private String password;
        private String name;
    }

    @Data
    public static class Login {
        @Email
        @NotBlank
        private String email;
        @NotBlank
        private String password;
    }
}

