package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.usuario.CreateUsuarioRequest;
import com.GuilhermeGuerra.biblioteca.dto.usuario.UpdateUsuarioRequest;
import com.GuilhermeGuerra.biblioteca.dto.usuario.UsuarioResponse;
import com.GuilhermeGuerra.biblioteca.entity.Usuario;
import com.GuilhermeGuerra.biblioteca.exceptions.AlreadyRegisteredException;
import com.GuilhermeGuerra.biblioteca.exceptions.BusinessException;
import com.GuilhermeGuerra.biblioteca.exceptions.NotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse findById(Long id) {
        Usuario usuario = findByIdOrElseThrow(id);
        return UsuarioResponse.fromEntity(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario findByIdOrElseThrow(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
    }

    @Transactional
    public Usuario criarUsuarioBase(CreateUsuarioRequest request) {

        if(usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new AlreadyRegisteredException("Usuário já cadastrado.");
        }

        LocalDateTime dataCadastro = LocalDateTime.now();

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(request.senha())
                .telefone(request.telefone())
                .dataCadastro(dataCadastro)
                .ativo(true)
                .build();

        usuarioRepository.save(usuario);
        return usuario;
    }

    @Transactional
    public UsuarioResponse create(CreateUsuarioRequest request) {
        Usuario usuario = criarUsuarioBase(request);
        return UsuarioResponse.fromEntity(usuario);
    }

    @Transactional
    public UsuarioResponse update(Long id, UpdateUsuarioRequest request) {
        Usuario usuario = findByIdOrElseThrow(id);

        if(request.email() != null && !request.email().isBlank() && !request.email().equalsIgnoreCase(usuario.getEmail())) {
            if(usuarioRepository.existsByEmailIgnoreCase(request.email())) {
                throw new AlreadyRegisteredException("O e-mail informado já está cadastrado.");
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
            usuario.setSenha(request.senha());
        }

        usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(usuario);
    }

    @Transactional
    public void desativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com o ID: " + id));
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
