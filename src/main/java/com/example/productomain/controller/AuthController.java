package com.example.productomain.controller;

import com.example.productomain.dto.LoginRequest;
import com.example.productomain.dto.LoginResponse;
import com.example.productomain.model.Usuario;
import com.example.productomain.security.JwtUtil;
import com.example.productomain.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales incorrectas");
        }

        UserDetails userDetails = usuarioService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generarToken(userDetails);

        Usuario usuario = usuarioService.buscarPorEmail(request.getEmail());

        return ResponseEntity.ok(new LoginResponse(token, usuario.getEmail(), usuario.getNombre(), usuario.getRol()));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody Usuario usuario) {
        try {
            Usuario nuevo = usuarioService.registrar(usuario);
            nuevo.setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al registrar: " + e.getMessage());
        }
    }
}
