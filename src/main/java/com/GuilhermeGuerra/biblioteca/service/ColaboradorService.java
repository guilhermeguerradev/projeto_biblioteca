package com.GuilhermeGuerra.biblioteca.service;

import com.GuilhermeGuerra.biblioteca.dto.colaborador.ColaboradorResponse;
import com.GuilhermeGuerra.biblioteca.dto.colaborador.CreateColaboradorRequest;
import com.GuilhermeGuerra.biblioteca.dto.colaborador.FilterColaboradorRequest;
import com.GuilhermeGuerra.biblioteca.dto.colaborador.UpdateColaboradorRequest;
import com.GuilhermeGuerra.biblioteca.dto.usuario.CreateUsuarioRequest;
import com.GuilhermeGuerra.biblioteca.dto.usuario.UpdateUsuarioRequest;
import com.GuilhermeGuerra.biblioteca.entity.Colaborador;
import com.GuilhermeGuerra.biblioteca.entity.Usuario;
import com.GuilhermeGuerra.biblioteca.exceptions.ColaboradorNotFoundException;
import com.GuilhermeGuerra.biblioteca.exceptions.NotFoundException;
import com.GuilhermeGuerra.biblioteca.repository.ColaboradorRepository;
import com.GuilhermeGuerra.biblioteca.specification.ColaboradorSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;

    private final UsuarioService usuarioService;


    @Transactional(readOnly = true)
    public Page<ColaboradorResponse> listarComFiltros(FilterColaboradorRequest filter) {

        Specification<Colaborador> colaborador = ColaboradorSpecification.comFiltros(filter);

        Page<Colaborador> paginasColaborador = colaboradorRepository.findAll(colaborador, filter.toPageable("usuario.nome"));

        return paginasColaborador.map(ColaboradorResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public Colaborador findByIdOrElseThrow(Long id) {
        return this.colaboradorRepository.findById(id).orElseThrow(() -> new NotFoundException("Colaborador não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<ColaboradorResponse> findAll() {
        return colaboradorRepository.findAll().stream().map(ColaboradorResponse::fromEntity).toList();
    }

    public ColaboradorResponse findById(Long id) {
        Colaborador colaborador = findByIdOrElseThrow(id);
        return ColaboradorResponse.fromEntity(colaborador);
    }

    @Transactional
    public ColaboradorResponse create(CreateColaboradorRequest request) {


        CreateUsuarioRequest usuarioDto = new CreateUsuarioRequest(
                request.nome(),
                request.email(),
                request.senha(),
                request.telefone()
        );

        Usuario usuario = usuarioService.criarUsuarioBase(usuarioDto);

        Colaborador colaborador = Colaborador.builder()
                .usuario(usuario)
                .setor(request.setor())
                .cargo(request.cargo())
                .build();

        Colaborador novoColaborador = colaboradorRepository.save(colaborador);

        return ColaboradorResponse.fromEntity(novoColaborador);

    }

    @Transactional
    public ColaboradorResponse update(Long id,UpdateColaboradorRequest request) {
        Colaborador colaborador = findByIdOrElseThrow(id);

        if(request.cargo() != null) {
            colaborador.setCargo(request.cargo());
        }

        if(request.setor() != null) {
            colaborador.setSetor(request.setor());
        }

        UpdateUsuarioRequest usuarioDto = new UpdateUsuarioRequest(
                request.nome(),
                request.telefone(),
                request.senha(),
                request.email()
        );

        usuarioService.update(id,usuarioDto);

        Colaborador updatedColaborador = colaboradorRepository.save(colaborador);

        return ColaboradorResponse.fromEntity(updatedColaborador);
    }

    @Transactional
    public void delete(Long id) {
        usuarioService.desativar(id);
    }
}
