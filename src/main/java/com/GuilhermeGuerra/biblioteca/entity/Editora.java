package com.GuilhermeGuerra.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "editora")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Editora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String pais;

    @OneToMany(mappedBy = "editora")
    private List<Livro> livros = new ArrayList<>();

}
