package com.GuilhermeGuerra.biblioteca.service;



import com.GuilhermeGuerra.biblioteca.dto.multa.MultaResponse;
import com.GuilhermeGuerra.biblioteca.entity.Emprestimo;
import com.GuilhermeGuerra.biblioteca.entity.Multa;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusEmprestimo;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusMulta;
import com.GuilhermeGuerra.biblioteca.exceptions.BusinessException;
import com.GuilhermeGuerra.biblioteca.exceptions.NotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.MultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MultaService {


    private final MultaRepository multaRepository;



    @Transactional(readOnly = true)
    public Multa findByIdOrElseThrow(Long id) {
        return this.multaRepository.findById(id).orElseThrow(() -> new NotFoundException("Multa não encontrada"));
    }

    @Transactional(readOnly = true)
    public MultaResponse findById(Long id) {
        Multa multa = findByIdOrElseThrow(id);
        return MultaResponse.fromEntity(multa);
    }

    @Transactional(readOnly = true)
    public List<MultaResponse> findAll() {
        return multaRepository.findAll().stream().map(MultaResponse::fromEntity).toList();
    }

    @Transactional
    public MultaResponse pagarMulta(Long id){
        Multa multa = findByIdOrElseThrow(id);

        if(multa.getStatus() == StatusMulta.PAGA) {
            throw new BusinessException("Está multa já foi paga");
        }

        multa.setStatus(StatusMulta.PAGA);
        multa.setDataPagamento(LocalDateTime.now());

        Emprestimo emprestimo = multa.getEmprestimo();
        if(emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
            emprestimo.setStatus(StatusEmprestimo.FINALIZADO);
        }
        return MultaResponse.fromEntity(multa);
    }

}
