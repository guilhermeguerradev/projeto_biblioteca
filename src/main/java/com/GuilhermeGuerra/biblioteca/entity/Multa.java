package com.GuilhermeGuerra.biblioteca.entity;

import com.GuilhermeGuerra.biblioteca.entity.enums.StatusMulta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "multa")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Multa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(
            name = "id_emprestimo",
            nullable = false,
            unique = true
    )
    private Emprestimo emprestimo;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMulta status;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;
}
