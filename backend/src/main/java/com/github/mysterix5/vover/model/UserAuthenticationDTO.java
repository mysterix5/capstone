package com.github.mysterix5.vover.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationDTO {
    private String username;
    private String password;
}