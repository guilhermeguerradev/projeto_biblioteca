package com.GuilhermeGuerra.biblioteca.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "usuario")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    private String telefone;

    @Column(nullable = false, insertable = false)
    private boolean ativo;

    @Column(name = "data_cadastro", nullable = false, insertable = false)
    private LocalDateTime dataCadastro;

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Professor professor;

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Aluno aluno;

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private Colaborador colaborador;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Reserva> reservas = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", fetch =  FetchType.LAZY)
    private List<Emprestimo> emprestimos = new ArrayList<>();

}