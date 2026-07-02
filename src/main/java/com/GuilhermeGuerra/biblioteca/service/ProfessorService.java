package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.request.professor.CreateProfessorRequest;
import com.GuilhermeGuerra.biblioteca.dto.request.professor.UpdateProfessorRequest;
import com.GuilhermeGuerra.biblioteca.dto.request.usuario.CreateUsuarioRequest;
import com.GuilhermeGuerra.biblioteca.dto.request.usuario.UpdateUsuarioRequest;
import com.GuilhermeGuerra.biblioteca.dto.response.ProfessorResponse;
import com.GuilhermeGuerra.biblioteca.entity.Professor;
import com.GuilhermeGuerra.biblioteca.entity.Usuario;
import com.GuilhermeGuerra.biblioteca.exceptions.BusinessException;
import com.GuilhermeGuerra.biblioteca.exceptions.ProfessorNotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    private final UsuarioService usuarioService;



    @Transactional
    public ProfessorResponse create(CreateProfessorRequest request) {
        if(professorRepository.existsBySiapeIgnoreCase(request.siape())){
            throw new BusinessException("Já existe um professor cadastrado com esse Siape.");
        }
        CreateUsuarioRequest usuarioDto = new CreateUsuarioRequest(
                request.nome(),
                request.email(),
                request.senha(),
                request.telefone()
        );

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

    @Transactional ProfessorResponse update(Long id, UpdateProfessorRequest request) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(()-> new ProfessorNotFoundException("Professor não encontrado."));

        if(request.departamento() != null && !request.departamento().isBlank()) {
            professor.setDepartamento(request.departamento());
        }

        if(request.titulacao() != null && !request.departamento().isBlank()) {
            professor.setTitulacao(request.titulacao());
        }

        UpdateUsuarioRequest usuarioDto = new UpdateUsuarioRequest(
                request.nome(),
                request.email(),
                request.senha(),
                request.telefone()
        );

        usuarioService.update(id, usuarioDto);

        Professor professorsalvo = professorRepository.save(professor);


        return ProfessorResponse.fromEntity(professorsalvo);
    }

    @Transactional
    public void delete(Long id) {
        usuarioService.desativar(id);
    }
}
