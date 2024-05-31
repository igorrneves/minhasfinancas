package br.com.ireis.minhasfinancas.modelo.repositorio;

import br.com.ireis.minhasfinancas.modelo.CartaoDeCredito;
import br.com.ireis.minhasfinancas.modelo.FaturaCartao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FaturaCartaoRepository  extends JpaRepository<FaturaCartao,Long> {
    @Query("select faturaCartao from FaturaCartao faturaCartao where faturaCartao.cartaoDeCredito = ?1 and faturaCartao.ano = ?2 and faturaCartao.mes = ?3")
    Optional<FaturaCartao> buscarPorCartaoMesAno(CartaoDeCredito cartaoDeCredito, int ano, int mes);

    @Query("select faturaCartao from FaturaCartao faturaCartao where faturaCartao.ano = ?1 and faturaCartao.mes = ?2")
    List<FaturaCartao> buscarPorMesAno(int ano, int mes);
}
