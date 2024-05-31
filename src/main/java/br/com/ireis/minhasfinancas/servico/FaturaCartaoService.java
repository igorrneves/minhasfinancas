package br.com.ireis.minhasfinancas.servico;

import br.com.ireis.minhasfinancas.excecoes.RegraNegocioException;
import br.com.ireis.minhasfinancas.modelo.CartaoDeCredito;
import br.com.ireis.minhasfinancas.modelo.FaturaCartao;
import br.com.ireis.minhasfinancas.modelo.GrupoLancamento;
import br.com.ireis.minhasfinancas.modelo.Lancamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.EstadoDaFaturaCartaoDeCredito;

import br.com.ireis.minhasfinancas.modelo.enumeracao.FormaDePagamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.TipoDeLancamento;
import br.com.ireis.minhasfinancas.modelo.repositorio.FaturaCartaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class FaturaCartaoService {
    private FaturaCartaoRepository repositorio;

    @Autowired
    private GrupoLancamentoService grupoLancamentoService;

    public FaturaCartaoService(FaturaCartaoRepository repositorio) {
        super();
        this.repositorio = repositorio;
    }

    @Transactional
    public FaturaCartao salvar(FaturaCartao faturaCartao) {
        validar(faturaCartao);
        return repositorio.saveAndFlush(faturaCartao);
    }

    @Transactional
    public FaturaCartao pagar(FaturaCartao faturaCartao) {
        validar(faturaCartao);
        BigDecimal valorPagoFatura = faturaCartao.getValorPagoFatura();
        if (faturaCartao.getValorTotalFatura().compareTo(faturaCartao.getValorPagoFatura()) > 0) {
            for (Lancamento lancamento : faturaCartao.getLancamentos()) {
                if (lancamento.getFormaDePagamento() == FormaDePagamento.DEBITO) { //Verifica se já foi realizado algum pagamento
                    valorPagoFatura = valorPagoFatura.add(lancamento.getValorLancamento());
                }
            }
            if (faturaCartao.getValorTotalFatura().compareTo(valorPagoFatura) > 0) {
                faturaCartao.setEstado(EstadoDaFaturaCartaoDeCredito.PARCIALMENTE_PAGA);
                faturaCartao.setSaldoCreditoRotativo(faturaCartao.getValorTotalFatura().subtract(valorPagoFatura));
            } else if (faturaCartao.getValorTotalFatura().compareTo(valorPagoFatura) == 0) {
                faturaCartao.setEstado(EstadoDaFaturaCartaoDeCredito.FECHADA);
                faturaCartao.setSaldoCreditoRotativo(BigDecimal.ZERO);
            }
        } else if (faturaCartao.getValorTotalFatura().compareTo(faturaCartao.getValorPagoFatura()) == 0) {
            faturaCartao.setEstado(EstadoDaFaturaCartaoDeCredito.FECHADA);
            faturaCartao.setSaldoCreditoRotativo(BigDecimal.ZERO);
        }
        return repositorio.saveAndFlush(faturaCartao);

    }

    @Transactional
    public FaturaCartao atualizar(FaturaCartao faturaCartao) {
        Objects.requireNonNull(faturaCartao.getId());
        validar(faturaCartao);
        return repositorio.saveAndFlush(faturaCartao);
    }

    public void deletarPorId(Long id) {
        Objects.requireNonNull(id);
        repositorio.deleteById(id);
    }

    public Optional<FaturaCartao> obterPorId(Long id) {
        Optional<FaturaCartao> faturaCartao = repositorio.findById(id);
        faturaCartao.get().setValorPagoFatura(BigDecimal.ZERO);
        faturaCartao.get().getLancamentos().forEach(lanc -> {
                    if (lanc.getTipoDeLancamento() == TipoDeLancamento.PAGAMENTO_FATURA_CARTAO) {
                        faturaCartao.get().setValorPagoFatura(faturaCartao.get().getValorPagoFatura().add(lanc.getValorLancamento()));
                    }

        });

        return faturaCartao;
    }

    public Optional<FaturaCartao> buscarPorCartaoMesAno(CartaoDeCredito cartaoDeCredito, int ano, int mes) {
        return repositorio.buscarPorCartaoMesAno(cartaoDeCredito, ano, mes);
    }

    public List<FaturaCartao> buscarPorMesAnoGrupo(int ano, int mes) {
        return repositorio.buscarPorMesAno(ano, mes);
    }


    public void validar(FaturaCartao faturaCartao) {
        if (faturaCartao.getMes() <= 0) {
            throw new RegraNegocioException("Informe um mês válido.");
        }
        if (faturaCartao.getAno() <= 0) {
            throw new RegraNegocioException("Informe um ano válido.");
        }
        if (faturaCartao.getId() == null) {
            if (buscarFaturaPorCartaoMesAnoSituacao(faturaCartao)) {
                throw new RegraNegocioException("Fatura de cartão já cadastrada.");
            }
        }

    }

    private boolean buscarFaturaPorCartaoMesAnoSituacao(FaturaCartao faturaCartao) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id", "lancamentos", "valorTotalFatura", "ValorPagoFatura", "dataPagamentoFatura", "estado")
                .withIncludeNullValues();

        Example<FaturaCartao> example = Example.of(faturaCartao, matcher);

        List<FaturaCartao> faturas = repositorio.findAll(example);
        if (faturas.size() > 0) {
            return true;
        }
        return false;
    }

    public List<FaturaCartao> buscarTodos() {
        List<FaturaCartao> faturas = repositorio.findAll(Sort.by(Sort.Direction.ASC, "ano", "mes"));
        return faturas;
    }

    public void calcularValorTotalDaFatura(FaturaCartao faturaCartao) {
        BigDecimal valorTotalDaFatura = BigDecimal.ZERO;
        if (faturaCartao.getLancamentos().size() > 0) {
            for (Lancamento lancamento : faturaCartao.getLancamentos()) {
                if (((lancamento.getTipoDeLancamento() == TipoDeLancamento.DESPESA) || (lancamento.getTipoDeLancamento() == TipoDeLancamento.PAGAMENTO_FATURA_CARTAO)) &&
                        (lancamento.getFormaDePagamento() == FormaDePagamento.CREDITO)) {
                    if (lancamento.getNumeroDeParcelas() > 1) {
                        valorTotalDaFatura = valorTotalDaFatura.add(lancamento.getValorParcelaLancamento());
                    } else {
                        valorTotalDaFatura = valorTotalDaFatura.add(lancamento.getValorLancamento());
                    }
                } else if (lancamento.getTipoDeLancamento() == TipoDeLancamento.ESTORNO_CARTAO) {
                    valorTotalDaFatura = valorTotalDaFatura.subtract(lancamento.getValorLancamento());
                }
            }
            faturaCartao.setValorTotalFatura(valorTotalDaFatura);
        }
    }

    @Transactional(readOnly = true)
    public List<FaturaCartao> buscarComFiltro(FaturaCartao faturaCartao) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id", "cartaoDeCredito", "lancamentos", "valorTotalFatura", "ValorPagoFatura", "dataPagamentoFatura", "saldoCreditoRotativo")
                .withIgnoreNullValues();
        if (faturaCartao.getEstado() == null) {
            matcher.withIgnorePaths("estado");
        }
        if (faturaCartao.getMes() == 0) {
            matcher.withIgnorePaths("mes");
        }
        if (faturaCartao.getAno() == 0) {
            matcher.withIgnorePaths("ano");
        }
        Example<FaturaCartao> example = Example.of(faturaCartao, matcher);

        List<FaturaCartao> faturas = repositorio.findAll(example, Sort.by(Sort.Direction.ASC, "ano"));

        return faturas;
    }
}
