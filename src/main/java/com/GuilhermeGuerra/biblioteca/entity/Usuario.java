package com.GuilhermeGuerra.biblioteca.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Entity
@Table(name = "usuario")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario implements UserDetails {

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id")
    )
    @Column(name = "role", length = 30)
    private Set<String> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    @Override
    public @Nullable String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }
}