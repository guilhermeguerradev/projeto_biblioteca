package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.request.LoginRequest;
import com.GuilhermeGuerra.biblioteca.dto.request.RefreshRequest;
import com.GuilhermeGuerra.biblioteca.dto.response.LoginResponse;
import com.GuilhermeGuerra.biblioteca.entity.RefreshToken;
import com.GuilhermeGuerra.biblioteca.entity.Usuario;
import com.GuilhermeGuerra.biblioteca.exceptions.TokenInvalidoException;
import com.GuilhermeGuerra.biblioteca.repository.RefreshTokenRepository;
import com.GuilhermeGuerra.biblioteca.repository.UsuarioRepository;
import com.GuilhermeGuerra.biblioteca.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário  não encontrado"));

        refreshTokenRepository.revogarTodosPorUsuario(usuario.getId());

        String accessToken = jwtService.gerarAccessToken(usuario);

        RefreshToken refreshToken = RefreshToken.builder()
                .usuario(usuario).token(UUID.randomUUID().toString())
                .expiraEm(jwtService.gerarExpirarEm())
                .revogado(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return new LoginResponse(accessToken, refreshToken.getToken());
    }

    public LoginResponse refresh(RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new TokenInvalidoException("Refresh token não encontrado"));

        if(refreshToken.isRevogado()) {
            throw new TokenInvalidoException("Refresh token revogado");
        }
        if(refreshToken.getExpiraEm().isBefore(Instant.now())) {
            throw new TokenInvalidoException("Refresh token expirado");
        }
        refreshToken.setRevogado(true);
        refreshTokenRepository.save(refreshToken);

        Usuario usuario = refreshToken.getUsuario();

        String novoAccessToken = jwtService.gerarAccessToken(usuario);

        RefreshToken novoRefreshToken  = RefreshToken.builder()
                .usuario(usuario)
                .token(UUID.randomUUID().toString())
                .expiraEm(jwtService.gerarExpirarEm())
                .revogado(false)
                .build();

        refreshTokenRepository.save(novoRefreshToken);

        return new LoginResponse(novoAccessToken,novoRefreshToken.getToken());

    }

    public void logout(RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new TokenInvalidoException("Refresh token não encontrado"));

        refreshToken.setRevogado(true);

        refreshTokenRepository.save(refreshToken);
    }
}
