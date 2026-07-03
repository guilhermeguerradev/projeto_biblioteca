package com.GuilhermeGuerra.biblioteca.entity;

import com.GuilhermeGuerra.biblioteca.entity.enums.Cargo;
import com.GuilhermeGuerra.biblioteca.entity.enums.Setor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "colaborador")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Colaborador {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name ="id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Cargo cargo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Setor setor;

    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao;

    @OneToMany(mappedBy = "colaborador", fetch = FetchType.LAZY)
    private List<Emprestimo> emprestimos = new ArrayList<>();
}
