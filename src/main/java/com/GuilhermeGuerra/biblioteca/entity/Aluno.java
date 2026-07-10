package com.GuilhermeGuerra.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aluno")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Aluno {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name ="id")
    private Usuario usuario;

    @Column(nullable = false, unique = true)
    private String matricula;

    private String curso;

    @Column(nullable = false)
    private byte periodo;

}
