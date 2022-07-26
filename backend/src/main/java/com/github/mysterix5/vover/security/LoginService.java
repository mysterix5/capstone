package com.github.mysterix5.vover.security;

import com.github.mysterix5.vover.model.security.LoginResponse;
import com.github.mysterix5.vover.model.security.UserAuthenticationDTO;
import com.github.mysterix5.vover.model.security.VoverUserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public LoginResponse login(VoverUserEntity user, UserAuthenticationDTO loginData) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword()));
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());
        return new LoginResponse(jwtService.createJwt(claims, user.getUsername()));
    }
}
