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
import com.GuilhermeGuerra.biblioteca.exceptions.BusinessException;
import com.GuilhermeGuerra.biblioteca.exceptions.NotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.EmprestimoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Service
public class EmprestimoService {

    private final EmprestimoRepository repository;

    private final UsuarioService usuarioService;

    private final ColaboradorService colaboradorService;

    private final ExemplarService exemplarService;

    public Emprestimo findByIdOrElseThrow(Long id) {
        return this.repository.findById(id).orElseThrow(() -> new NotFoundException("Empréstimo não encontrado"));
    }


    @Transactional
    public EmprestimoResponse create(CreateEmprestimoRequest request) {

        Usuario usuario = usuarioService.findByIdOrElseThrow(request.usuarioId());

        Colaborador colaborador = colaboradorService.findByIdOrElseThrow(request.colaboradorId());

        Exemplar exemplar = exemplarService.findByIdOrElseThrow(request.exemplarId());

        LocalDateTime dataEmprestimo = LocalDateTime.now();
        LocalDateTime dataPrevista = dataEmprestimo.plusDays(7);

        if (usuario.getProfessor() != null) {
            dataPrevista = dataEmprestimo.plusDays(14);
        }

        int emprestimosAtivos = repository.countByUsuarioIdAndStatus(usuario.getId(), StatusEmprestimo.ATIVO);

        if(emprestimosAtivos > 3) {
            throw new BusinessException("O usuário já atingiu o limite de empréstimos ativos");
        }

        StatusEmprestimo status;
        if(request.status() == null) {
            status = StatusEmprestimo.ATIVO;
        } else {
            status = request.status();
        }

        UpdateExemplarRequest exemplarDTO = new UpdateExemplarRequest(StatusExemplar.EMPRESTADO);

        exemplarService.update(exemplar.getId(), exemplarDTO);

        Emprestimo emprestimo = Emprestimo.builder()
                .usuario(usuario)
                .colaborador(colaborador)
                .exemplar(exemplar)
                .dataEmprestimo(dataEmprestimo)
                .dataPrevista(dataPrevista)
                .quantidadeRenovacoes(0)
                .status(status)
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
        emprestimo.setDataDevolucao(novaDataPrevista);

        return EmprestimoResponse.fromEntity(emprestimo);
    }

    @Transactional
    public EmprestimoResponse finalizarEmprestimo(Long id, FinalizarEmprestimoRequest request) {
        Emprestimo emprestimo = findByIdOrElseThrow(id);

        if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO || emprestimo.getStatus() == StatusEmprestimo.CANCELADO) {
            throw new BusinessException("Este empréstimo já foi finalizado ou devolvido");
        }

        LocalDateTime dataDevolucao = LocalDateTime.now();
        emprestimo.setDataDevolucao(dataDevolucao);
        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);

        long diasAtraso = ChronoUnit.DAYS.between(emprestimo.getDataPrevista(), dataDevolucao);

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

        UpdateExemplarRequest exemplarDTO = new UpdateExemplarRequest(request.statusExemplar());

        exemplarService.update(emprestimo.getExemplar().getId(), exemplarDTO);

        return EmprestimoResponse.fromEntity(emprestimo);
    }
}
