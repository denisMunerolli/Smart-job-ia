package com.smartjobai.api.controller;

import com.smartjobai.api.dto.AuthRequest;
import com.smartjobai.api.dto.AuthResponse;
import com.smartjobai.api.security.JwtTokenProvider;
import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String accessToken  = tokenProvider.generateToken(auth);
        String refreshToken = tokenProvider.generateRefreshToken(auth);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, tokenProvider.getExpirationMs()));
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.cadastrar(usuario));
    }

    /**
     * POST /api/auth/refresh
     * Recebe o refresh token e retorna um novo access token.
     * Evita que o usuário precise fazer login novamente.
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        try {
            if (!tokenProvider.validateToken(request.refreshToken())) {
                return ResponseEntity.status(401).build();
            }
            String newAccessToken = tokenProvider.refreshAccessToken(request.refreshToken());
            return ResponseEntity.ok(new AuthResponse(newAccessToken, request.refreshToken(), tokenProvider.getExpirationMs()));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    public record RefreshRequest(String refreshToken) {}
}
