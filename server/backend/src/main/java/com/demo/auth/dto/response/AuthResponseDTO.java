package com.demo.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;       // JWT token — frontend stores this
    private String name;        // user's name
    private String email;       // user's email
    private String role;        // USER or ADMIN
}