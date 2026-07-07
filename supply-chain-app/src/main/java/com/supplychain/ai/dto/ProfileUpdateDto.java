package com.supplychain.ai.dto;

import lombok.Data;

@Data
public class ProfileUpdateDto {
    private String firstName;
    private String lastName;
    private String email;
    private String newPassword;
    private String confirmPassword;
}
