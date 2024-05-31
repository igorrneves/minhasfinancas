package br.com.ireis.minhasfinancas.modelo.repositorio;

import br.com.ireis.minhasfinancas.modelo.Investimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestimentoRepository extends JpaRepository<Investimento,Long> {
}
