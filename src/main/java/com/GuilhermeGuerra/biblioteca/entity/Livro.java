package com.GuilhermeGuerra.biblioteca.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "livro")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private int anoPublicacao;

    @Column(nullable = false)
    private String edicao;

    @Column(nullable = false)
    private String idioma;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String sinopse;

    private int numeroPaginas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_editora", nullable = false)
    private Editora editora;

    @OneToMany(mappedBy = "livro", fetch = FetchType.LAZY)
    private List<Reserva> reservas = new ArrayList<>();



    @OneToMany(mappedBy = "livro",fetch = FetchType.LAZY)
    private List<Exemplar> exemplares = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "livro_autor",
            joinColumns = @JoinColumn(name = "id_livro"),
            inverseJoinColumns = @JoinColumn(name = "id_autor")
    )
    private List<Categoria> categorias = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "livro_categoria",
            joinColumns = @JoinColumn(name = "id_livro"),
            inverseJoinColumns = @JoinColumn(name = "id_autor")
    )
    private List<Autor> autores = new ArrayList<>();
}
