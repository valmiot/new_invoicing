package com.tsh.erechnung.controller;

import com.tsh.erechnung.config.JwtUtil;
import com.tsh.erechnung.dto.UserDto;
import com.tsh.erechnung.model.User;
import com.tsh.erechnung.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto.Register dto) {
        User user = userService.register(dto.getEmail(), dto.getPassword(), dto.getName());
        return ResponseEntity.ok(Map.of("id", user.getId(), "email", user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDto.Login dto) {
        User user = userService.authenticate(dto.getEmail(), dto.getPassword());
        String token = jwtUtil.generateToken(user.getId());
        return ResponseEntity.ok(Map.of("access_token", token, "token_type", "bearer"));
    }
}
