package br.com.ireis.minhasfinancas.modelo.repositorio;

import br.com.ireis.minhasfinancas.modelo.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface LancamentoRepository extends JpaRepository<Lancamento,Long> {
    Optional<Lancamento> findByData(LocalDate data);

    boolean existsByEmail(LocalDate data);

}
