package com.ordersmanagement.crm.controllers;

import com.ordersmanagement.crm.models.forms.LoginForm;
import com.ordersmanagement.crm.models.response.JwtResponse;
import com.ordersmanagement.crm.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginForm credentials) {
        return new ResponseEntity<>(authService.trySignIn(credentials), HttpStatus.OK);
    }
}