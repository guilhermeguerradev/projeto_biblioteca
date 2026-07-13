package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.professor.CreateProfessorRequest;
import com.GuilhermeGuerra.biblioteca.dto.professor.UpdateProfessorRequest;
import com.GuilhermeGuerra.biblioteca.dto.usuario.CreateUsuarioRequest;
import com.GuilhermeGuerra.biblioteca.dto.usuario.UpdateUsuarioRequest;
import com.GuilhermeGuerra.biblioteca.dto.professor.ProfessorResponse;
import com.GuilhermeGuerra.biblioteca.entity.Professor;
import com.GuilhermeGuerra.biblioteca.entity.Usuario;
import com.GuilhermeGuerra.biblioteca.exceptions.AlreadyRegisteredException;
import com.GuilhermeGuerra.biblioteca.exceptions.BusinessException;
import com.GuilhermeGuerra.biblioteca.exceptions.NotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    private final UsuarioService usuarioService;



    @Transactional(readOnly = true)
    public List<ProfessorResponse> findAll() {
        return professorRepository.findAll().stream().map(ProfessorResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public ProfessorResponse findById(Long id) {
        Professor professor = this.professorRepository.findById(id).orElseThrow(() -> new NotFoundException(("Professor não encontrado.")));
        return ProfessorResponse.fromEntity(professor);
    }

    @Transactional
    public ProfessorResponse create(CreateProfessorRequest request) {
        if(professorRepository.existsBySiapeIgnoreCase(request.siape())){
            throw new AlreadyRegisteredException("Já existe um professor cadastrado com esse Siape.");
        }

        CreateUsuarioRequest usuarioDto = CreateUsuarioRequest.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(request.senha())
                .telefone(request.telefone())
                .build();

        Usuario usuario = usuarioService.criarUsuarioBase(usuarioDto);

        Professor professor = Professor.builder()
                .id(usuario.getId())
                .usuario(usuario)
                .siape(request.siape())
                .departamento(request.departamento())
                .titulacao(request.titulacao())
                .build();

        professorRepository.save(professor);

        return ProfessorResponse.fromEntity(professor);
    }

    @Transactional
    public ProfessorResponse update(Long id, UpdateProfessorRequest request) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Professor não encontrado."));

        if(request.departamento() != null && !request.departamento().isBlank()) {
            professor.setDepartamento(request.departamento());
        }

        if(request.titulacao() != null && !request.titulacao().isBlank()) {
            professor.setTitulacao(request.titulacao());
        }

        UpdateUsuarioRequest usuarioDto = UpdateUsuarioRequest.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(request.senha())
                .telefone(request.telefone())
                .build();

        usuarioService.update(id, usuarioDto);

        return ProfessorResponse.fromEntity(professor);
    }

    @Transactional
    public void desativar(Long id) {
        usuarioService.desativar(id);
    }
}
