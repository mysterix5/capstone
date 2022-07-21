package com.github.mysterix5.vover.security;

import com.github.mysterix5.vover.model.LoginResponse;
import com.github.mysterix5.vover.model.VoverUser;
import com.github.mysterix5.vover.model.UserAuthenticationDTO;
import com.github.mysterix5.vover.usermanagement.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final UserService userService;
    private final LoginService loginService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody UserAuthenticationDTO registerData){
        try {
            userService.createUser(registerData);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody UserAuthenticationDTO loginData) {
        try{
            VoverUser user = userService.findByUsername(loginData.getUsername()).orElseThrow();
            return ResponseEntity.ok(loginService.login(user, loginData));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
