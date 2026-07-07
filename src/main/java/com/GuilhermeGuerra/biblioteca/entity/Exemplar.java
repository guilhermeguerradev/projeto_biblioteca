package com.GuilhermeGuerra.biblioteca.entity;

import com.GuilhermeGuerra.biblioteca.entity.enums.StatusExemplar;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exemplar")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Exemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "livro_id",
            nullable = false
    )
    private Livro livro;

    @Column(name = "codigo_patrimonio", nullable = false, unique = true)
    private String codigoPatrimonio;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false , length = 15)
    private StatusExemplar status;


    private String localizacao;

    @Column(name = "data_aquisicao")
    private LocalDate dataAquisicao;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @OneToMany(mappedBy = "exemplar" , fetch = FetchType.LAZY)
    private List<Emprestimo> emprestimos = new ArrayList<>();

}
