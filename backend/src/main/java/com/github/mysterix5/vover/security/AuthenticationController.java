package com.github.mysterix5.vover.security;

import com.github.mysterix5.vover.model.*;
import com.github.mysterix5.vover.usermanagement.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final UserService userService;
    private final LoginService loginService;

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody UserRegisterDTO registerData){
        try {
            userService.createUser(registerData);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (MultipleSubErrorException e){
            log.warn("registering user {} failed", registerData, e);
            return ResponseEntity.badRequest().body(new VoverErrorDTO(e));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserAuthenticationDTO loginData) {
        try{
            VoverUser user = userService.findByUsername(loginData.getUsername()).orElseThrow();
            return ResponseEntity.ok(loginService.login(user, loginData));
        }catch(NoSuchElementException e){
            log.warn("login user {} failed", loginData, e);
            return ResponseEntity.badRequest().body(new VoverErrorDTO("Login failed", "This user does not exist"));
        }catch(BadCredentialsException e){
            log.warn("login user {} failed", loginData, e);
            return ResponseEntity.badRequest().body(new VoverErrorDTO("Login failed", "It was not possible to authenticate this 'user', 'password' combination", "Are you sure your credentials are correct?"));
        }
    }

}