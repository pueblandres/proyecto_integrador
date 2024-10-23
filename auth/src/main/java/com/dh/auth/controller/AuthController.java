package com.dh.auth.controller;

import com.dh.auth.dto.UserDto;
import com.dh.auth.service.UserService;
import com.dh.auth.util.JwtUtil;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RestTemplate restTemplate) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) {
        UserDto dto = userService.saveUser(userDto);
        UserDetails userDetails = userService.loadUserByUsername(dto.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        String accountServiceUrl = "http://localhost:8081/accounts/create/" + dto.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwt);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange(accountServiceUrl, HttpMethod.POST, entity, String.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDto userDto) {
        UserDetails userDetails = userService.loadUserByUsername(userDto.getEmail());
        if (passwordEncoder.matches(userDto.getPassword(), userDetails.getPassword())) {
            String jwt = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(jwt);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Remover "Bearer " del token
        jwtUtil.revokeToken(jwt);
        return ResponseEntity.ok("Logout exitoso");
    }
}
