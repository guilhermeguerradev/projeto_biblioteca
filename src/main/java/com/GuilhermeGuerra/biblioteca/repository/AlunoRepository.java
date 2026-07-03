package com.GuilhermeGuerra.biblioteca.repository;

import com.GuilhermeGuerra.biblioteca.entity.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno,Long>, JpaSpecificationExecutor<Aluno> {
    boolean existsByMatriculaIgnoreCase(String matricula);

    @Query(value = "SELECT NEXT VALUE FOR seq_matricula_aluno", nativeQuery = true)
    Long getProximoNumeroSequencial();
}
