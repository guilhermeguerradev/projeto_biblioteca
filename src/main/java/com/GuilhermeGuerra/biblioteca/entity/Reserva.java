package com.GuilhermeGuerra.biblioteca.entity;

import com.GuilhermeGuerra.biblioteca.entity.enums.StatusReserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reserva",
        uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_reserva_usuario_livro",
                columnNames = {"id_usuario", "id_livro"}
        )})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reserva {

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
            name = "id_livro",
            nullable = false
    )
    private Livro livro;

    @Column(name = "data_reserva", nullable = false, insertable = false)
    private LocalDateTime dataReserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusReserva statusReserva;


}
