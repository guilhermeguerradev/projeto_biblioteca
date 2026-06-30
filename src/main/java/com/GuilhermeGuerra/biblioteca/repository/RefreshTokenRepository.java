package com.GuilhermeGuerra.biblioteca.repository;

import com.GuilhermeGuerra.biblioteca.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);

    // Revoga todos os tokens ativos do usuário antes de emitir um novo no login
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken r SET r.revogado = true WHERE r.usuario.id = :usuarioId AND r.revogado = false")
    void revogarTodosPorUsuario(Long usuarioId);
}
