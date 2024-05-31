package br.com.ireis.minhasfinancas.modelo.repositorio;

import br.com.ireis.minhasfinancas.modelo.Banco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BancoRepository extends JpaRepository<Banco,Long> {
}
