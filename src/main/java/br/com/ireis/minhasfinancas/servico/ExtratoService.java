package br.com.ireis.minhasfinancas.servico;

import br.com.ireis.minhasfinancas.modelo.*;
import br.com.ireis.minhasfinancas.modelo.enumeracao.EstadoDoLancamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.FormaDePagamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.TipoDeLancamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ExtratoService {

    @Autowired
    private BancoService service;
    @Autowired
    private LancamentoService lancamentoService;
    @Autowired
    private CartaoDeCreditoService cartaoDeCreditoService;
    @Autowired
    private FaturaCartaoService faturaCartaoService;

    public List<Banco> calcularTotalMensalDebitoCreditoPorBanco(int ano, int mes) {
        List<Banco> bancos = service.buscarTodosBancos();
        List<Lancamento> lancamentos = lancamentoService.buscarPorMesAno(ano, mes);

        BigDecimal valorReceitaContaPorBanco;
        BigDecimal valorDespesaContaPorBanco;
        BigDecimal valorInvestimentoPorBanco;

        for (Banco banco : bancos) {
            valorReceitaContaPorBanco = BigDecimal.ZERO;
            valorDespesaContaPorBanco = BigDecimal.ZERO;
            valorInvestimentoPorBanco = BigDecimal.ZERO;
            for (Lancamento lancamento : lancamentos) {
                if (lancamento.getBanco().getId() == banco.getId()) {
                    if ((lancamento.getTipoDeLancamento() == TipoDeLancamento.RECEITA) && lancamento.getEstadoDoLancamento() == EstadoDoLancamento.VALIDADO) {
                        valorReceitaContaPorBanco = valorReceitaContaPorBanco.add(lancamento.getValorLancamento());
                        banco.getLancamentosCreditoRealizados().add(lancamento);
                    }

                    if (((lancamento.getTipoDeLancamento() == TipoDeLancamento.DESPESA) || (lancamento.getTipoDeLancamento() == TipoDeLancamento.INVESTIMENTO) ||
                            (lancamento.getTipoDeLancamento() == TipoDeLancamento.PAGAMENTO_FATURA_CARTAO)) && (lancamento.getEstadoDoLancamento() == EstadoDoLancamento.VALIDADO)) {
                        if (lancamento.getFormaDePagamento() == FormaDePagamento.DEBITO || lancamento.getFormaDePagamento() == FormaDePagamento.PIX || lancamento.getFormaDePagamento() == FormaDePagamento.CHEQUE) {
                            valorDespesaContaPorBanco = valorDespesaContaPorBanco.add(lancamento.getValorLancamento());
                            if (lancamento.getTipoDeLancamento() == TipoDeLancamento.INVESTIMENTO){
                                valorInvestimentoPorBanco = valorInvestimentoPorBanco.add(lancamento.getValorLancamento());
                                banco.getLancamentosInvestimentoRealizados().add(lancamento);
                            }
                            banco.getLancamentosDebitosRealizados().add(lancamento);
                        }

                    }
                }
                banco.setTotalCredito(valorReceitaContaPorBanco);
                banco.setTotalDebito(valorDespesaContaPorBanco);
                banco.setTotalInvestimento(valorInvestimentoPorBanco);
                calcularSaldoDisponivelConta(banco);
            }
        }
        return bancos;
    }

    public Banco calcularRealizadoMensalPorBanco(int ano, int mes, long bancoId) {
        List<Banco> bancos = calcularTotalMensalDebitoCreditoPorBanco(ano, mes);
        Banco banco = new Banco();
        for (Banco bancoSelecionado : bancos) {
            if (bancoSelecionado.getId() == bancoId) {
                banco = bancoSelecionado;
                break;
            }
        }
        return banco;
    }

    public List<CartaoDeCredito> calcularTotalMensalDebitoCreditoPorCartao(int ano, int mes) {
        List<CartaoDeCredito> cartoes = cartaoDeCreditoService.buscarTodos();

        for (CartaoDeCredito cartao : cartoes) {
            var fatura = faturaCartaoService.buscarPorCartaoMesAno(cartao, ano, mes);
            if (fatura.isPresent()) {
                cartao.setSaldo(fatura.get().getValorTotalFatura());
            } else {
                cartao.setSaldo(BigDecimal.ZERO);
            }

        }
        return cartoes;
    }

    public List<Lancamento> listaMensalDebitoCreditoRealizado(int ano, int mes) {
        List<Lancamento> lancamentos = lancamentoService.buscarPorMesAno(ano, mes); //representa os lançamentos à vista
        List<FaturaCartao> faturas = faturaCartaoService.buscarPorMesAnoGrupo(ano, mes);
        for (FaturaCartao faturaCartao : faturas) {
            for (Lancamento lancamento : faturaCartao.getLancamentos()) {
                lancamentos.add(lancamento);
            }
        }
        return lancamentos;
    }

    public void calcularSaldoDisponivelConta(Banco banco) {
        banco.setSaldoDisponivel(banco.getTotalCredito().subtract(banco.getTotalDebito()));
    }
}
