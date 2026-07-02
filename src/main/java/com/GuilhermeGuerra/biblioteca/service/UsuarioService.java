package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.request.usuario.CreateUsuarioRequest;
import com.GuilhermeGuerra.biblioteca.dto.request.usuario.UpdateUsuarioRequest;
import com.GuilhermeGuerra.biblioteca.dto.response.UsuarioResponse;
import com.GuilhermeGuerra.biblioteca.entity.Usuario;
import com.GuilhermeGuerra.biblioteca.entity.enums.TipoRole;
import com.GuilhermeGuerra.biblioteca.exceptions.BusinessException;
import com.GuilhermeGuerra.biblioteca.exceptions.EmailJaCadastradoException;
import com.GuilhermeGuerra.biblioteca.exceptions.UsuarioJaCadastradoException;
import com.GuilhermeGuerra.biblioteca.exceptions.UsuarioNotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::fromEntity).toList();
    }
    public UsuarioResponse findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado."));
        return UsuarioResponse.fromEntity(usuario);
    }

    public UsuarioResponse create(CreateUsuarioRequest request) {

        if(usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new UsuarioJaCadastradoException("Usuário já cadastrado.");
        }

        Set<String> roles = request.roles().stream().map(TipoRole::name).collect(Collectors.toSet());

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(request.senha())
                .telefone(request.telefone())
                .roles(roles)
                .build();

        usuarioRepository.save(usuario);

        return UsuarioResponse.fromEntity(usuario);
    }

    public UsuarioResponse update(Long id, UpdateUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(()-> new UsuarioNotFoundException("Usuário não encontrado."));

        if(request.email() != null && !request.email().isBlank() && !request.email().equalsIgnoreCase(usuario.getEmail()) {
            if(usuarioRepository.existsByEmailIgnoreCase(request.email())) {
                throw new EmailJaCadastradoException("O e-mail informado já está cadastrado.");
            }
            usuario.setEmail(request.email());
        }

        if (request.nome() != null && !request.nome().isBlank()) {
            usuario.setNome(request.nome());
        }
        if (request.telefone() != null) {
            usuario.setTelefone(request.telefone());
        }

        if (request.senha() != null && !request.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }

        if (request.roles() != null && !request.roles().isEmpty()) {
            Set<String> novasRoles = request.roles().stream()
                    .map(TipoRole::name)
                    .collect(Collectors.toSet());

            usuario.getRoles().clear();
            usuario.getRoles().addAll(novasRoles);
        }
        usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(usuario);
    }

    @Transactional
    public void desativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuário não encontrado com o ID: " + id));
        if (!usuario.isAtivo()) {
            throw new BusinessException("Este usuário já se encontra desativado no sistema.");
        }
        boolean possuiPendencias = usuario.getEmprestimos().stream()
                .anyMatch(emprestimo -> emprestimo.getDataDevolucao() == null);

        if (possuiPendencias) {
            throw new BusinessException("Não é possível desativar o usuário pois ele possui empréstimos pendentes de devolução.");
        }
        usuario.setAtivo(false);

        usuarioRepository.save(usuario);
    }

}
