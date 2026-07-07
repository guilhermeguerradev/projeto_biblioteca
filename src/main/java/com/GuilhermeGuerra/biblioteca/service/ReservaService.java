package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.reserva.CreateReservaRequest;
import com.GuilhermeGuerra.biblioteca.dto.reserva.ReservaResponse;
import com.GuilhermeGuerra.biblioteca.dto.reserva.UpdateReservaRequest;
import com.GuilhermeGuerra.biblioteca.entity.Livro;
import com.GuilhermeGuerra.biblioteca.entity.Reserva;
import com.GuilhermeGuerra.biblioteca.entity.Usuario;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusReserva;
import com.GuilhermeGuerra.biblioteca.exceptions.NotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Service
public class ReservaService {

    private final ReservaRepository repository;

    private final UsuarioService usuarioService;

    private final LivroService livroService;


    @Transactional(readOnly = true)
    public Reserva findByIdOrElseThrow(Long id) {
        return this.repository.findById(id).orElseThrow(() -> new NotFoundException("Reserva não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> findAll() {
        return this.repository.findAll().stream().map(ReservaResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponse findById(Long id) {
        Reserva reserva = findByIdOrElseThrow(id);
        return ReservaResponse.fromEntity(reserva);
    }

    @Transactional
    public ReservaResponse create(CreateReservaRequest request) {

        Livro livro = livroService.findByIdOrElseThrow(request.livroId());

        Usuario usuario = usuarioService.findByIdOrElseThrow(request.usuarioId());

        StatusReserva statusReserva;
        if(request.status() == null) {
            statusReserva = StatusReserva.ATIVA;
        } else {
            statusReserva = request.status();
        }

        Reserva reserva = Reserva.builder()
                .livro(livro)
                .usuario(usuario)
                .status(statusReserva)
                .build();

        this.repository.save(reserva);

        return ReservaResponse.fromEntity(reserva);
    }

    @Transactional
    public ReservaResponse update(Long id,UpdateReservaRequest request) {
        Reserva reserva = findByIdOrElseThrow(id);

        if(request.status() != null && request.status() != reserva.getStatus()) {
            reserva.setStatus(request.status());
        }

        return ReservaResponse.fromEntity(reserva);

    }

    @Transactional
    public void expirar(Long id) {
        Reserva reserva = findByIdOrElseThrow(id);
        reserva.setStatus(StatusReserva.EXPIRADA);
    }
}
