package com.GuilhermeGuerra.biblioteca.repository;

import com.GuilhermeGuerra.biblioteca.entity.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ColaboradorRepository extends JpaRepository<Colaborador,Long> , JpaSpecificationExecutor<Colaborador> {
}
