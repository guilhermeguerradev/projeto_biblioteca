package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.exemplar.UpdateExemplarRequest;
import com.GuilhermeGuerra.biblioteca.dto.reserva.CreateReservaRequest;
import com.GuilhermeGuerra.biblioteca.dto.reserva.ReservaResponse;
import com.GuilhermeGuerra.biblioteca.dto.reserva.UpdateReservaRequest;
import com.GuilhermeGuerra.biblioteca.entity.*;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusEmprestimo;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusExemplar;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusMulta;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusReserva;
import com.GuilhermeGuerra.biblioteca.exceptions.BusinessException;
import com.GuilhermeGuerra.biblioteca.exceptions.NotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.EmprestimoRepository;
import com.GuilhermeGuerra.biblioteca.repository.LivroRepository;
import com.GuilhermeGuerra.biblioteca.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioService usuarioService;
    private final LivroService livroService;
    private final ExemplarService exemplarService;
    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;

    @Transactional(readOnly = true)
    public Reserva findByIdOrElseThrow(Long id) {
        return this.reservaRepository.findById(id).orElseThrow(() -> new NotFoundException("Reserva não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> findAll() {
        return this.reservaRepository.findAll().stream().map(ReservaResponse::fromEntity).toList();
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

        if(reservaRepository.existsByLivroIdAndUsuarioIdAndStatus(request.livroId(), request.usuarioId(), StatusReserva.ATIVA)) {
            throw new BusinessException("Você já possui uma reserva ativa para este livro.");
        }

        if(emprestimoRepository.usuarioJaPossuiOLivroEmprestado(request.usuarioId(), request.livroId()))  {
            throw new BusinessException("Não é possível reservar um livro que você já possui um empréstimo ativo.");
        }

        if(livroRepository.possuiExemplarDisponivel(request.livroId())) {
            throw new BusinessException("Este livro possui exemplares disponíveis na estante. Faça um empréstimo.");
        }

        if(!livroRepository.possuiExemplaresCirculando(request.livroId())) {
            throw new BusinessException("Não há exemplares válidos circulando para este livro.");
        }

        if(emprestimoRepository.countByUsuarioIdAndStatus(request.usuarioId(), StatusEmprestimo.ATIVO) >= 3) {
            throw new BusinessException("O Usuário já atingiu o limite máximo de 3 empréstimos ativos.");
        }

        if(emprestimoRepository.possuiMultasPendentes(request.usuarioId())) {
            throw new BusinessException("O Usuário possui multas pendentes no sistema.");
        }

        if(emprestimoRepository.existsByUsuarioIdAndStatus(request.usuarioId(), StatusEmprestimo.ATRASADO)) {
            throw new BusinessException("O Usuário possui empréstimos atrasados no sistema.");
        }

        Reserva reserva = Reserva.builder()
                .livro(livro)
                .usuario(usuario)
                .status(StatusReserva.ATIVA)
                .dataReserva(LocalDateTime.now())
                .build();

        reservaRepository.save(reserva);

        return ReservaResponse.fromEntity(reserva);
    }

    @Transactional
    public ReservaResponse update(Long id,UpdateReservaRequest request) {
        Reserva reserva = findByIdOrElseThrow(id);

        if (reserva.getStatus() == StatusReserva.FINALIZADA
                || reserva.getStatus() == StatusReserva.CANCELADA || reserva.getStatus() == StatusReserva.EXPIRADA) {
            throw new BusinessException("Não é possível alterar o status de uma reserva que já foi finalizada ou cancelada.");
        }

        if(request.status() == StatusReserva.CANCELADA) {
            if(reserva.getStatus() == StatusReserva.AGUARDANDO_RETIRADA) {
                passarLivroParaProximoOuLiberarEstante(reserva.getLivro().getId());
            }
            reserva.setStatus(StatusReserva.CANCELADA);
        } else if (request.status() != null && request.status() != reserva.getStatus()) {
            reserva.setStatus(request.status());
        }

        return ReservaResponse.fromEntity(reserva);

    }

    @Transactional
    public void expirar(Long id) {
        Reserva reserva = findByIdOrElseThrow(id);

        if (reserva.getStatus() == StatusReserva.FINALIZADA || reserva.getStatus() == StatusReserva.CANCELADA) {
            throw new BusinessException("Não é possível expirar uma reserva que já foi finalizada ou cancelada.");
        }
        if (reserva.getStatus() == StatusReserva.EXPIRADA) {
            throw new BusinessException("Esta reserva já se encontra expirada.");
        }

        reserva.setStatus(StatusReserva.EXPIRADA);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void liberarReservasExpiradas() {
        LocalDateTime limiteDeRetirada = LocalDateTime.now().minusDays(3);

        List<Reserva> reservasExpiradas = reservaRepository.findAllByStatusAndDataLiberacaoBefore(
                StatusReserva.AGUARDANDO_RETIRADA, limiteDeRetirada);

        for (Reserva reserva : reservasExpiradas) {
            reserva.setStatus(StatusReserva.EXPIRADA);

            passarLivroParaProximoOuLiberarEstante(reserva.getLivro().getId());
        }
    }

    private void passarLivroParaProximoOuLiberarEstante(Long livroId) {
        boolean promovida = promoverProximaReservaAtiva(livroId);

        if (!promovida) {
            exemplarService.marcarComoDisponivelPorLivro(livroId);
        }
    }

    @Transactional
    public boolean promoverProximaReservaAtiva(Long livroId) {
        Optional<Reserva> proximoDaFilaOpt = reservaRepository
                .findFirstByLivroIdAndStatusOrderByDataReservaAsc(livroId, StatusReserva.ATIVA);

        if (proximoDaFilaOpt.isEmpty()) {
            return false;
        }

        Reserva proximaReserva = proximoDaFilaOpt.get();
        proximaReserva.setStatus(StatusReserva.AGUARDANDO_RETIRADA);
        proximaReserva.setDataLiberacao(LocalDateTime.now());
        return true;
    }

    @Transactional(readOnly = true)
    public boolean existeReservaAtivaParaLivro(Long livroId) {
        return reservaRepository.existsByLivroIdAndStatus(livroId, StatusReserva.ATIVA);
    }

    @Transactional
    public void resolverReservaNoBalcao(Long livroId, Long usuarioId) {
        Reserva reserva = reservaRepository
                .findFirstByLivroIdAndStatusOrderByDataReservaAsc(livroId, StatusReserva.AGUARDANDO_RETIRADA)
                .orElseThrow(() -> new BusinessException("Inconsistência: Livro marcado como reservado, mas nenhuma reserva foi encontrada."));

        boolean ehOProprioDono = reserva.getUsuario().getId().equals(usuarioId);
        boolean prazoExpirou = reserva.getDataLiberacao().isBefore(LocalDateTime.now().minusDays(3));

        if (ehOProprioDono) {
            reserva.setStatus(StatusReserva.FINALIZADA);
        } else if (prazoExpirou) {
            reserva.setStatus(StatusReserva.EXPIRADA);
        } else {
            throw new BusinessException("Este livro está reservado para outro usuário dentro do prazo de 3 dias.");
        }
    }
}
