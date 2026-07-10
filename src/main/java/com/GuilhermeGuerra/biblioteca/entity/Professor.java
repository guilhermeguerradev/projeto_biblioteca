package com.GuilhermeGuerra.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name ="professor")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Professor {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name ="id")
    private Usuario usuario;

    @Column(nullable = false, unique = true)
    private String siape;

    private String departamento;

    private String titulacao;
}
