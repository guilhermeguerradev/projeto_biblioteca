package com.GuilhermeGuerra.biblioteca.specification;


import com.GuilhermeGuerra.biblioteca.dto.colaborador.FilterColaboradorRequest;
import com.GuilhermeGuerra.biblioteca.entity.Colaborador;
import com.GuilhermeGuerra.biblioteca.entity.Usuario;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public final class ColaboradorSpecification {

    private ColaboradorSpecification() {}


    public static Specification<Colaborador> comFiltros(FilterColaboradorRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            From<Colaborador, Usuario> usuarioJoin;

            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                Fetch<Colaborador, Usuario> fetch = root.fetch("usuario", JoinType.INNER);
                usuarioJoin = (From<Colaborador, Usuario>) fetch; // Mapeia para 'From' para permitir criar os filtros abaixo
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

            if(filter.setor() != null) {
                predicates.add(cb.equal(root.get("setor"), filter.setor()));
            }

            if (filter.cargo() != null) {
                predicates.add(cb.equal(root.get("cargo"), filter.cargo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
