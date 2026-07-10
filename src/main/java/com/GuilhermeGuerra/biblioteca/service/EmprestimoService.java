package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.emprestimo.CreateEmprestimoRequest;
import com.GuilhermeGuerra.biblioteca.dto.emprestimo.EmprestimoResponse;
import com.GuilhermeGuerra.biblioteca.dto.emprestimo.FinalizarEmprestimoRequest;
import com.GuilhermeGuerra.biblioteca.dto.emprestimo.UpdateEmprestimoRequest;
import com.GuilhermeGuerra.biblioteca.dto.exemplar.UpdateExemplarRequest;
import com.GuilhermeGuerra.biblioteca.entity.*;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusEmprestimo;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusExemplar;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusMulta;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusReserva;
import com.GuilhermeGuerra.biblioteca.exceptions.BusinessException;
import com.GuilhermeGuerra.biblioteca.exceptions.NotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.EmprestimoRepository;
import com.GuilhermeGuerra.biblioteca.repository.ExemplarRepository;
import com.GuilhermeGuerra.biblioteca.repository.LivroRepository;
import com.GuilhermeGuerra.biblioteca.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EmprestimoService {

    private final EmprestimoRepository repository;

    private final UsuarioService usuarioService;

    private final ColaboradorService colaboradorService;

    private final ReservaRepository reservaRepository;

    private final ExemplarRepository exemplarRepository;
    private final ExemplarService exemplarService;

    private final LivroService livroService;

    @Transactional
    public EmprestimoResponse create(CreateEmprestimoRequest request) {

        Usuario usuario = usuarioService.findByIdOrElseThrow(request.usuarioId());
        Colaborador colaborador = colaboradorService.findByIdOrElseThrow(request.colaboradorId());
        Livro livro = livroService.findByIdOrElseThrow(request.livroId());

        validarRestricoesUsuario(request.usuarioId());

        Exemplar exemplar = exemplarRepository.findFirstByLivroIdAndStatusIn(livro.getId(), List.of(StatusExemplar.DISPONIVEL, StatusExemplar.RESERVADO))
                .orElseThrow(() -> new NotFoundException("Não há exemplares disponíveis para este livro."));

        if(exemplar.getStatus() == StatusExemplar.RESERVADO) {
            processarReservaNoBalcao(livro.getId(), usuario.getId());
        }
        exemplar.setStatus(StatusExemplar.EMPRESTADO);

        LocalDateTime dataEmprestimo = LocalDateTime.now();
        LocalDateTime dataPrevista = usuario.getProfessor() == null ? dataEmprestimo.plusDays(7) : dataEmprestimo.plusDays(14);

        Emprestimo emprestimo = Emprestimo.builder()
                .usuario(usuario)
                .colaborador(colaborador)
                .exemplar(exemplar)
                .dataEmprestimo(dataEmprestimo)
                .dataPrevista(dataPrevista)
                .quantidadeRenovacoes(0)
                .status(StatusEmprestimo.ATIVO)
                .build();

        this.repository.save(emprestimo);

        return EmprestimoResponse.fromEntity(emprestimo);
    }

    @Transactional
    public EmprestimoResponse update(Long id,UpdateEmprestimoRequest request) {
        Emprestimo emprestimo = findByIdOrElseThrow(id);

        if(request.status() != null && request.status() != emprestimo.getStatus()) {
                emprestimo.setStatus(request.status());

                if(request.status() == StatusEmprestimo.DEVOLVIDO && emprestimo.getDataDevolucao() == null) {
                    emprestimo.setDataDevolucao(LocalDateTime.now());
                }
            }

        if(request.dataDevolucao() != null) {
            emprestimo.setDataDevolucao(request.dataDevolucao());

            if(emprestimo.getStatus() != StatusEmprestimo.DEVOLVIDO) {
                emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
            }
        }

        return EmprestimoResponse.fromEntity(emprestimo);
    }

    @Transactional
    public EmprestimoResponse realizarRenovacao(Long id) {
        Emprestimo emprestimo = findByIdOrElseThrow(id);

        int quantidadeRenovacoes = emprestimo.getQuantidadeRenovacoes();

        if (quantidadeRenovacoes >= 3) {
            throw new BusinessException("Usuário já atingiu o limite de renovações neste empréstimo");
        }

        if(emprestimo.getStatus() != StatusEmprestimo.ATIVO) {
            throw new BusinessException("Não é possível renovar empréstimo que não está ativo");
        }

        emprestimo.setQuantidadeRenovacoes(quantidadeRenovacoes + 1);

        LocalDateTime novaDataPrevista = emprestimo.getDataPrevista().plusDays(7);
        emprestimo.setDataPrevista(novaDataPrevista);

        return EmprestimoResponse.fromEntity(emprestimo);
    }

    @Transactional
    public EmprestimoResponse finalizarEmprestimo(Long id, FinalizarEmprestimoRequest request) {

        Emprestimo emprestimo = this.devolver(id, request.statusExemplar());

        long diasAtraso = ChronoUnit.DAYS.between(emprestimo.getDataPrevista().toLocalDate(), emprestimo.getDataDevolucao().toLocalDate());

        boolean temAtraso = diasAtraso > 0;
        boolean temDano = request.statusExemplar() == StatusExemplar.DANIFICADO || request.statusExemplar() == StatusExemplar.EXTRAVIADO;

        if(temAtraso || temDano) {
            BigDecimal valorTotal = BigDecimal.ZERO;
            StringBuilder motivoBuilder = new StringBuilder();
            if(temAtraso) {
                valorTotal = valorTotal.add(BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(diasAtraso)));
                motivoBuilder.append("Atraso de ").append(diasAtraso).append(" dias.");
            }
            if(temDano) {
                valorTotal = valorTotal.add(BigDecimal.valueOf(100));
                motivoBuilder.append("Exemplar entregue ").append(request.statusExemplar()).append(".");
            }
            Multa multa = Multa.builder()
                    .emprestimo(emprestimo)
                    .valor(valorTotal)
                    .motivo(motivoBuilder.toString().trim())
                    .status(StatusMulta.PENDENTE)
                    .dataPagamento(null)
                    .build();

            emprestimo.setMulta(multa);
        }

        emprestimo.setStatus(StatusEmprestimo.FINALIZADO);
        return EmprestimoResponse.fromEntity(emprestimo);
    }

    private Emprestimo devolver(Long id, StatusExemplar statusExemplar) {
        Emprestimo emprestimo = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Empréstimo não encontrado."));

        if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO
                || emprestimo.getStatus() == StatusEmprestimo.FINALIZADO) {
            throw new BusinessException("Este empréstimo já foi devolvido anteriormente.");
        }

        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        emprestimo.setDataDevolucao(LocalDateTime.now());

        Exemplar exemplar = emprestimo.getExemplar();

        if(statusExemplar == StatusExemplar.DANIFICADO || statusExemplar == StatusExemplar.EXTRAVIADO) {
            exemplar.setStatus(statusExemplar);
        } else {
            Optional<Reserva> proximaReservaOpt = reservaRepository
                    .findFirstByLivroIdAndStatusOrderByDataReservaAsc(exemplar.getLivro().getId(), StatusReserva.ATIVA);

            if (proximaReservaOpt.isPresent()) {
                Reserva reservaGanhadora = proximaReservaOpt.get();
                reservaGanhadora.setStatus(StatusReserva.AGUARDANDO_RETIRADA);
                reservaGanhadora.setDataLiberacao(LocalDateTime.now());
                exemplar.setStatus(StatusExemplar.RESERVADO);
            } else {
                exemplar.setStatus(StatusExemplar.DISPONIVEL);
            }
        }
        return emprestimo;
    }

    private void validarRestricoesUsuario(Long usuarioId) {
        if (repository.possuiMultasPendentes(usuarioId)) {
            throw new BusinessException("O usuário possui multas pendentes.");
        }
        if (repository.countByUsuarioIdAndStatus(usuarioId, StatusEmprestimo.ATIVO) >= 3) {
            throw new BusinessException("O usuário atingiu o limite de 3 empréstimos ativos.");
        }
    }

    private void processarReservaNoBalcao(Long livroId, Long usuarioId) {
        Reserva reserva = reservaRepository.findFirstByLivroIdAndStatusOrderByDataReservaAsc(livroId, StatusReserva.AGUARDANDO_RETIRADA)
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

    public Emprestimo findByIdOrElseThrow(Long id) {
        return this.repository.findById(id).orElseThrow(() -> new NotFoundException("Empréstimo não encontrado"));
    }
}
