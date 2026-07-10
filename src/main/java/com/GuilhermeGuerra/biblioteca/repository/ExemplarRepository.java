package com.GuilhermeGuerra.biblioteca.repository;

import com.GuilhermeGuerra.biblioteca.entity.Exemplar;
import com.GuilhermeGuerra.biblioteca.entity.enums.StatusExemplar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExemplarRepository extends JpaRepository<Exemplar,Long> {

    Optional<Exemplar> findFirstByLivroIdAndStatus(Long livroId, StatusExemplar status);
    Optional<Exemplar> findFirstByLivroIdAndStatusIn(Long livroId, List<StatusExemplar> statusPermitidos);
}
