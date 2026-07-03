package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.request.aluno.CreateAlunoRequest;
import com.GuilhermeGuerra.biblioteca.dto.request.aluno.UpdateAlunoRequest;
import com.GuilhermeGuerra.biblioteca.dto.request.usuario.CreateUsuarioRequest;
import com.GuilhermeGuerra.biblioteca.dto.request.usuario.UpdateUsuarioRequest;
import com.GuilhermeGuerra.biblioteca.dto.response.AlunoResponse;
import com.GuilhermeGuerra.biblioteca.entity.Aluno;
import com.GuilhermeGuerra.biblioteca.entity.Usuario;
import com.GuilhermeGuerra.biblioteca.exceptions.AlunoNotFoundException;
import com.GuilhermeGuerra.biblioteca.exceptions.BusinessException;
import com.GuilhermeGuerra.biblioteca.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
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
    public List<AlunoResponse> findAll() {
        return alunoRepository.findAll().stream().map(AlunoResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public AlunoResponse findById(Long id) {
        Aluno aluno = alunoRepository.findById(id).orElseThrow(()-> new AlunoNotFoundException("Aluno não encontrado"));
        return AlunoResponse.fromEntity(aluno);
    }


    @Transactional
    public AlunoResponse create(CreateAlunoRequest request) {
        if(alunoRepository.existsByMatriculaIgnoreCase(request.matricula())) {
            throw new BusinessException("Matrícula já cadastrada");
        }

        CreateUsuarioRequest usuarioDto = new CreateUsuarioRequest(
                request.nome(),
                request.email(),
                request.senha(),
                request.telefone()
        );

        Usuario usuario = usuarioService.criarUsuarioBase(usuarioDto);

        Aluno aluno = Aluno.builder()
                .usuario(usuario)
                .matricula(gerarMatricula())
                .curso(request.curso())
                .periodo(request.periodo())
                .periodo(request.periodo())
                .build();

        alunoRepository.save(aluno);

        return AlunoResponse.fromEntity(aluno);

    }

    @Transactional
    public AlunoResponse update(Long id,UpdateAlunoRequest request) {

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(()-> new AlunoNotFoundException("Aluno não encontrado"));

        if(request.matricula() != null && !request.matricula().isBlank() && !request.matricula().equalsIgnoreCase(aluno.getMatricula())) {
            aluno.setMatricula(request.matricula());
        }

        if(request.periodo() != null && request.periodo() > 0) {
            aluno.setPeriodo(request.periodo());
        }

        if(request.curso() != null && !request.curso().isBlank()) {
            aluno.setCurso(request.curso());
        }

        UpdateUsuarioRequest usuarioDto = new UpdateUsuarioRequest(
                request.nome(),
                request.email(),
                request.senha(),
                request.telefone()
        );

        usuarioService.update(id, usuarioDto);

        alunoRepository.save(aluno);

        return AlunoResponse.fromEntity(aluno);

    }

    @Transactional
    public void delete(Long id) {
        usuarioService.desativar(id);
    }


    private String gerarMatricula() {
        Long sequencial = alunoRepository.getProximoNumeroSequencial();

        int anoAtualInvertido = LocalDate.now().getYear() % 100;

        String numeroFormatado = String.format("%06d", sequencial);

        return String.format("MG %02d %s", anoAtualInvertido, numeroFormatado);
    }
}
