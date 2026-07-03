package com.GuilhermeGuerra.biblioteca.specification;


import com.GuilhermeGuerra.biblioteca.dto.aluno.AlunoFilterRequest;
import com.GuilhermeGuerra.biblioteca.entity.Aluno;
import com.GuilhermeGuerra.biblioteca.entity.Usuario;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class AlunoSpecification {



    private AlunoSpecification() {}

    public static Specification<Aluno> comFiltros(AlunoFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            From<Aluno, Usuario> usuarioJoin;

            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                Fetch<Aluno, Usuario> fetch = root.fetch("usuario", JoinType.INNER);
                usuarioJoin = (From<Aluno, Usuario>) fetch; // Mapeia para 'From' para permitir criar os filtros abaixo
            } else {
                usuarioJoin = root.join("usuario", JoinType.INNER);
            }

            if (StringUtils.hasText(filter.nome())) {
                predicates.add(cb.like(
                        cb.lower(usuarioJoin.get("nome")),
                        "%" + filter.nome().toLowerCase() + "%"
                ));
            }

            if (filter.ativo() != null) {
                predicates.add(cb.equal(usuarioJoin.get("ativo"), filter.ativo()));
            }

            if (StringUtils.hasText(filter.matricula())) {
                predicates.add(cb.like(
                        cb.lower(root.get("matricula")),
                        "%" + filter.matricula().toLowerCase() + "%"
                ));
            }

            if (StringUtils.hasText(filter.curso())) {
                predicates.add(cb.like(
                        cb.lower(root.get("curso")),
                        "%" + filter.curso().toLowerCase() + "%"
                ));
            }

            if (filter.periodo() != null) {
                predicates.add(cb.equal(root.get("periodo"), filter.periodo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }



}
