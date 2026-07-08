package com.GuilhermeGuerra.biblioteca.entity;

import com.GuilhermeGuerra.biblioteca.entity.enums.StatusEmprestimo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "emprestimo")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_usuario",
            nullable = false
    )
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "id_colaborador",
            nullable = false
    )
    private Colaborador colaborador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_exemplar",
            nullable = false
    )
    private Exemplar exemplar;

    @Column(name = "data_emprestimo", nullable = false)
    private LocalDateTime dataEmprestimo;

    @Column(name = "data_prevista", nullable = false)
    private LocalDateTime dataPrevista;

    @Column(name = "data_devolucao")
    private LocalDateTime dataDevolucao;

    @Column(name = "quantidade_renovacoes", nullable = false)
    private int quantidadeRenovacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEmprestimo status;

    @OneToOne(mappedBy = "emprestimo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Multa multa;


}
