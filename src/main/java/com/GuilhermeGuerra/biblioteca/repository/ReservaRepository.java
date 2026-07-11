package com.GuilhermeGuerra.biblioteca.repository;

import com.GuilhermeGuerra.biblioteca.entity.Reserva;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva,Long> {

    boolean existsByLivroIdAndUsuarioIdAndStatus(Long livroId, Long usuarioId, StatusReserva status);

    boolean existsByLivroIdAndStatus(Long livroId, StatusReserva status);

    Optional<Reserva> findFirstByLivroIdAndStatusOrderByDataReservaAsc(Long livroId, StatusReserva status);

    List<Reserva> findAllByStatusAndDataLiberacaoBefore(StatusReserva status, LocalDateTime limite);
}
