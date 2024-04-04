package com.vedalingo.controllers;


import com.vedalingo.dtos.LoginUserDto;
import com.vedalingo.dtos.RegisterUserDto;
import com.vedalingo.exceptions.CustomAuthenticationException;
import com.vedalingo.models.User;
import com.vedalingo.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;


    // login user api
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginUserDto loginUserDto){
        System.out.println("Request from front-end side--"+loginUserDto.getEmail()+", "+loginUserDto.getPassword());
        try {
            User user = authService.authenticateUser(loginUserDto);
            return ResponseEntity.ok().build();
        } catch ( CustomAuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email or password is incorrect.");
        }
    }

    // register user  api
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserDto registerUserDto) {
        System.out.println("user registered successfully --");
        try {
            this.authService.registerUser(registerUserDto);
            return ResponseEntity.ok().build();
        }
        catch ( CustomAuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email is already exist. Try another !");
        }
    }

    //forget password
//    @GetMapping("/forget")
//    public ResponseEntity<?> getPassword(@PathVariable String email) throws CustomAuthenticationException {
//        try {
//            String password=this.authService.getPassword(email);
//            return  ResponseEntity.ok().body(password);//actually send password to Gmail
//        }
//        catch ( CustomAuthenticationException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
//        }
//
//    }



}
