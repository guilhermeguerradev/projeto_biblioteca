package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.aluno.FilterAlunoRequest;
import com.GuilhermeGuerra.biblioteca.dto.aluno.CreateAlunoRequest;
import com.GuilhermeGuerra.biblioteca.dto.aluno.UpdateAlunoRequest;
import com.GuilhermeGuerra.biblioteca.dto.usuario.CreateUsuarioRequest;
import com.GuilhermeGuerra.biblioteca.dto.usuario.UpdateUsuarioRequest;
import com.GuilhermeGuerra.biblioteca.dto.aluno.AlunoResponse;
import com.GuilhermeGuerra.biblioteca.entity.Aluno;
import com.GuilhermeGuerra.biblioteca.entity.Usuario;
import com.GuilhermeGuerra.biblioteca.exceptions.NotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.AlunoRepository;
import com.GuilhermeGuerra.biblioteca.specification.AlunoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoRepository alunoRepository;

    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public Page<AlunoResponse> listarComFiltros(FilterAlunoRequest filter) {
        Specification<Aluno> aluno = AlunoSpecification.comFiltros(filter);

        Page<Aluno> paginaAlunos = alunoRepository.findAll(aluno, filter.toPageable("usuario.nome"));

        return paginaAlunos.map(AlunoResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<AlunoResponse> findAll() {
        return alunoRepository.findAll().stream().map(AlunoResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public AlunoResponse findById(Long id) {
        Aluno aluno = alunoRepository.findById(id).orElseThrow(()-> new NotFoundException("Aluno não encontrado"));
        return AlunoResponse.fromEntity(aluno);
    }


    @Transactional
    public AlunoResponse create(CreateAlunoRequest request) {

        CreateUsuarioRequest usuarioDto = CreateUsuarioRequest.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(request.senha())
                .telefone(request.telefone())
                .build();

        Usuario usuario = usuarioService.criarUsuarioBase(usuarioDto);

        Aluno aluno = Aluno.builder()
                .id(usuario.getId())
                .usuario(usuario)
                .matricula(gerarMatricula())
                .curso(request.curso())
                .periodo(request.periodo())
                .build();

        alunoRepository.save(aluno);

        return AlunoResponse.fromEntity(aluno);

    }

    @Transactional
    public AlunoResponse update(Long id,UpdateAlunoRequest request) {

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Aluno não encontrado"));

        if(request.periodo() != null && request.periodo() > 0) {
            aluno.setPeriodo(request.periodo());
        }

        if(request.curso() != null && !request.curso().isBlank()) {
            aluno.setCurso(request.curso());
        }

        UpdateUsuarioRequest usuarioDto = UpdateUsuarioRequest.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(request.senha())
                .telefone(request.telefone())
                .build();

        usuarioService.update(id, usuarioDto);

        return AlunoResponse.fromEntity(aluno);

    }

    @Transactional
    public void desativar(Long id) {
        usuarioService.desativar(id);
    }


    private String gerarMatricula() {
        Long sequencial = alunoRepository.getProximoNumeroSequencial();

        int anoAtualInvertido = LocalDate.now().getYear() % 100;

        String numeroFormatado = String.format("%06d", sequencial);

        return String.format("MG %02d %s", anoAtualInvertido, numeroFormatado);
    }
}
