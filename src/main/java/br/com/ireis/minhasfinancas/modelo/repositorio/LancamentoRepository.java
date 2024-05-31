package br.com.ireis.minhasfinancas.modelo.repositorio;

import br.com.ireis.minhasfinancas.modelo.Banco;
import br.com.ireis.minhasfinancas.modelo.CartaoDeCredito;
import br.com.ireis.minhasfinancas.modelo.GrupoLancamento;
import br.com.ireis.minhasfinancas.modelo.Lancamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.EstadoDoLancamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.FormaDePagamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.TipoDeLancamento;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface LancamentoRepository extends JpaRepository<Lancamento,Long>, JpaSpecificationExecutor<Lancamento> {
    @Query("select lancamento from Lancamento lancamento where year(lancamento.dataDoLancamento) = ?1 and month(lancamento.dataDoLancamento) = ?2 " +
            "and lancamento.grupoLancamento = ?3 and lancamento.estadoDoLancamento = 'VALIDADO'  and lancamento.tipoDeLancamento = 'DESPESA' order by lancamento.dataDoLancamento")
    List<Lancamento> buscarPorMesAnoEstadoGrupo(int year, int month, GrupoLancamento grupoLancamento);
    @Query("select lancamento from Lancamento lancamento where lancamento.estadoDoLancamento = ?1 order by lancamento.dataDoLancamento")
    List<Lancamento> buscarPorEstadoLancamento(EstadoDoLancamento estadoDoLancamento);
    @Query("select lancamento from Lancamento lancamento where year(lancamento.dataDoLancamento) = ?1 and month(lancamento.dataDoLancamento) = ?2 " +
            "and lancamento.banco = ?3 and lancamento.formaDePagamento = ?4 order by lancamento.dataDoLancamento")
    List<Lancamento> buscarPorMesAnoEstadoBancoFormaDePagamento(int year, int month, Banco banco, FormaDePagamento formaDePagamento);
    @Query("select lancamento from Lancamento lancamento where year(lancamento.dataDoLancamento) = ?1 and month(lancamento.dataDoLancamento) = ?2 " +
            "and lancamento.tipoDeLancamento = ?3 order by lancamento.dataDoLancamento")
    List<Lancamento> buscarPorMesAnoTipoDeLancamento(int year, int month, TipoDeLancamento tipoDeLancamento);
    @Query("select lancamento from Lancamento lancamento where year(lancamento.dataDoLancamento) = ?1 and month(lancamento.dataDoLancamento) = ?2 order by lancamento.dataDoLancamento")
    List<Lancamento> buscarPorMesAno(int year, int month);

    @Query("select SUM(lancamento.valorLancamento) from Lancamento lancamento where year(lancamento.dataDoLancamento) = ?1 " +
            "and month(lancamento.dataDoLancamento) = ?2 and lancamento.tipoDeLancamento = ?3 and " +
            "lancamento.numeroDeParcelas=1 and lancamento.estadoDoLancamento='VALIDADO' " +
            "GROUP BY( lancamento.tipoDeLancamento) ")
    BigDecimal buscarTotalPorTipoMesAno(int year, int month, TipoDeLancamento tipo);

    @Query("select SUM(lancamento.valorLancamento) from Lancamento lancamento where year(lancamento.dataDoLancamento) = ?1 " +
            "and month(lancamento.dataDoLancamento) = ?2 and lancamento.tipoDeLancamento = 'DESPESA' and " +
            "lancamento.numeroDeParcelas=1 and lancamento.estadoDoLancamento='VALIDADO' " +
            "and lancamento.grupoLancamento = ?3" +
            "GROUP BY( lancamento.tipoDeLancamento) ")
    BigDecimal buscarTotalPorGrupoMesAno(int year, int month,GrupoLancamento grupoLancamento);

    @Query("select lancamento from Lancamento lancamento where lancamento.cartaoDeCredito = ?1 and lancamento.estadoDoLancamento = ?2 order by lancamento.dataDoLancamento")
    List<Lancamento> buscarPorCartaoDeCreditoEstado(CartaoDeCredito cartaoDeCredito, EstadoDoLancamento estadoDoLancamento);
}
