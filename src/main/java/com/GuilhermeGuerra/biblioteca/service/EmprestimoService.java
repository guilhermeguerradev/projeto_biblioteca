package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.emprestimo.CreateEmprestimoRequest;
import com.GuilhermeGuerra.biblioteca.dto.emprestimo.EmprestimoResponse;
import com.GuilhermeGuerra.biblioteca.dto.emprestimo.FinalizarEmprestimoRequest;
import com.GuilhermeGuerra.biblioteca.dto.emprestimo.UpdateEmprestimoRequest;
import com.GuilhermeGuerra.biblioteca.entity.*;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusEmprestimo;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusExemplar;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusMulta;
import com.GuilhermeGuerra.biblioteca.exceptions.BusinessException;
import com.GuilhermeGuerra.biblioteca.exceptions.NotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.EmprestimoRepository;
import com.GuilhermeGuerra.biblioteca.repository.ExemplarRepository;
import com.GuilhermeGuerra.biblioteca.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EmprestimoService {

    private final EmprestimoRepository repository;
    private final UsuarioService usuarioService;
    private final ColaboradorService colaboradorService;
    private final ReservaService reservaService;
    private final ExemplarRepository exemplarRepository;
    private final LivroService livroService;

    @Transactional
    public EmprestimoResponse create(CreateEmprestimoRequest request) {

        Usuario usuario = usuarioService.findByIdOrElseThrow(request.usuarioId());
        Colaborador colaborador = colaboradorService.findByIdOrElseThrow(request.colaboradorId());
        Livro livro = livroService.findByIdOrElseThrow(request.livroId());

        validarRestricoesUsuario(request.usuarioId());

        Exemplar exemplar = exemplarRepository.findFirstByLivroIdAndStatusIn(livro.getId(), List.of(StatusExemplar.DISPONIVEL, StatusExemplar.RESERVADO))
                .orElseThrow(() -> new NotFoundException("Não há exemplares disponíveis para este livro."));

        if (this.repository.usuarioJaPossuiOLivroEmprestado(usuario.getId(), livro.getId())) {
            throw new BusinessException("Não é possível fazer empréstimo de um livro que você já possui um empréstimo ativo.");
        }

        if(exemplar.getStatus() == StatusExemplar.RESERVADO) {
            reservaService.resolverReservaNoBalcao(livro.getId(), usuario.getId());
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

            if(request.status() == StatusEmprestimo.DEVOLVIDO || request.status() == StatusEmprestimo.FINALIZADO) {
                throw new BusinessException("Para devolver ou finalizar um empréstimo, utilize o fluxo de devolução (finalizarEmprestimo).");
            }

            if(emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO || emprestimo.getStatus() == StatusEmprestimo.FINALIZADO) {
                throw new BusinessException("Não é possível alterar o status de um empréstimo já devolvido ou finalizado.");
            }

            emprestimo.setStatus(request.status());
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

        if(emprestimo.getDataPrevista().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Não é possível renovar um empréstimo atrasado. Efetue a devolução.");
        }

        Long livroId = emprestimo.getExemplar().getLivro().getId();
        if(reservaService.existeReservaAtivaParaLivro(livroId)) {
            throw new BusinessException("Não é possível renovar: existe uma reserva ativa aguardando este livro.");
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
        } else {
            emprestimo.setStatus(StatusEmprestimo.FINALIZADO);

        }

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
            boolean promovida = reservaService.promoverProximaReservaAtiva(exemplar.getLivro().getId());
            exemplar.setStatus(promovida ? StatusExemplar.RESERVADO : StatusExemplar.DISPONIVEL);
        }
        return emprestimo;
    }

    private void validarRestricoesUsuario(Long usuarioId) {
        if (repository.possuiMultasPendentes(usuarioId)) {
            throw new BusinessException("O usuário possui multas pendentes.");
        }
        if (repository.existsByUsuarioIdAndStatus(usuarioId, StatusEmprestimo.ATRASADO)) {
            throw new BusinessException("O usuário possui empréstimos atrasados.");
        }
        if (repository.countByUsuarioIdAndStatus(usuarioId, StatusEmprestimo.ATIVO) >= 3) {
            throw new BusinessException("O usuário atingiu o limite de 3 empréstimos ativos.");
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void marcarEmprestimosAtrasados() {
        List<Emprestimo> emprestimosVencidos = repository.findAllByStatusAndDataPrevistaBefore(
                StatusEmprestimo.ATIVO, LocalDateTime.now());

        for (Emprestimo emprestimo : emprestimosVencidos) {
            emprestimo.setStatus(StatusEmprestimo.ATRASADO);
        }
    }

    public Emprestimo findByIdOrElseThrow(Long id) {
        return this.repository.findById(id).orElseThrow(() -> new NotFoundException("Empréstimo não encontrado"));
    }
}
