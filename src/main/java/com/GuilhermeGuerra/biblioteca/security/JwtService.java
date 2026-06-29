package com.GuilhermeGuerra.biblioteca.security;

import com.GuilhermeGuerra.biblioteca.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
//gerar e validar token
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    @Value("${jwt.refresh-expiration-days}")
    private long refreshExpirationDays;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build().parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpirado(String token) {
        return extrairClaims(token).getExpiration().before(new Date());
    }

    public List<GrantedAuthority> extrairAuthorities(String token) {
        List<String> roles = extrairClaims(token).get("roles", List.class);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    public Instant gerarExpirarEm() {
        return Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS);
    }

    public boolean isTokenValido(String token, UserDetails userDetails) {
            String email = extrairEmail(token);
            return email.equals(userDetails.getUsername()) && !isTokenExpirado(token);
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    public String gerarAccessToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("role", usuario.getRoles())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }




}
