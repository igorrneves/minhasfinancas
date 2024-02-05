package br.com.ireis.minhasfinancas.servico.implementacao;

import br.com.ireis.minhasfinancas.excecoes.RegraNegocioException;
import br.com.ireis.minhasfinancas.modelo.GrupoLancamento;
import br.com.ireis.minhasfinancas.modelo.Lancamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.FormaDePagamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.FormaDeRecebimentoCredito;
import br.com.ireis.minhasfinancas.modelo.enumeracao.TipoDeLancamento;
import br.com.ireis.minhasfinancas.modelo.repositorio.LancamentoRepository;
import br.com.ireis.minhasfinancas.servico.LancamentoService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.*;

@Service
public class LancamentoServiceImplementacao implements LancamentoService {

    private LancamentoRepository repositorio;
    public LancamentoServiceImplementacao(LancamentoRepository repositorio) {
        super();
        this.repositorio = repositorio;
    }
    @Override
    @Transactional
    public Lancamento salvar(Lancamento lancamento) throws CloneNotSupportedException{
        validar(lancamento);
        if (lancamento.getNumeroDeRepeticaoDoLancamento() > 0) {
            for (int i=1; lancamento.getNumeroDeRepeticaoDoLancamento() > i; i++) {
                Lancamento repeticaoLancamento = (Lancamento) lancamento.clone();
                LocalDate dataDoLancamento = repeticaoLancamento.getDataDoLancamento();
                repeticaoLancamento.setDataDoLancamento(dataDoLancamento.plusMonths(i));
                repositorio.saveAndFlush(repeticaoLancamento);
            }
        }
        return repositorio.saveAndFlush(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        //validar(lancamento);
        return repositorio.save(lancamento);

    }

    @Override
    public void deletar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        repositorio.delete(lancamento);
    }

    @Override
    public void deletarPorId(Long id) {
        Objects.requireNonNull(id);
        repositorio.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lancamento> buscar(Lancamento lancamentoFiltro) {

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id","usuario",
                        "dataDoLancamento","descricaoLancamento","valorLancamento","valorParcelaLancamento","numeroDeParcelas","tipoDeLancamento","banco",
                        "tipoDeDespesa","formaDePagamento","numeroCheque","grupoLancamento","estadoDoLancamento","parcela")
                .withIncludeNullValues();

        Example<Lancamento> example = Example.of(lancamentoFiltro, matcher);

        List<Lancamento> lancamentos =  repositorio.findAll(example);

        return lancamentos;
    }

    @Transactional(readOnly = true)
    public List<Lancamento> buscarPorMesAnoEstadoGrupo(int ano, int mes, GrupoLancamento grupoLancamento) {
        return repositorio.buscarPorMesAnoEstadoGrupo(ano,mes, grupoLancamento);
    }

    @Transactional(readOnly = true)
    public List<Lancamento> buscarPorMesAno(int ano, int mes) {
        return repositorio.buscarPorMesAno(ano,mes);
    }

    @Transactional(readOnly = true)
    public List<Lancamento> buscarTodos() {
         List<Lancamento> lancamentos =  repositorio.findAll(Sort.by(Sort.Direction.ASC,"dataDoLancamento"));
        return lancamentos;
    }

    @Override
    public void validar(Lancamento lancamento) {
        if(lancamento.getDescricaoLancamento() == null || lancamento.getDescricaoLancamento().trim().equals("")){
            throw new RegraNegocioException("Informe uma Descrição válida.");
        }
        if(lancamento.getDataDoLancamento() ==null ){
            throw new RegraNegocioException("Informe uma Data válida.");
        }
        if(lancamento.getNumeroDeParcelas() < 1 ){
            throw new RegraNegocioException("Informe a quantidade de parcelas.");
        } else if (lancamento.getNumeroDeParcelas() > 1) {
            try {
                criarParcelas(lancamento);
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null){
            throw new RegraNegocioException("Informe um Usuário.");
        }
        if(lancamento.getValorLancamento() == null || lancamento.getValorLancamento().compareTo(BigDecimal.ZERO) < 1 ){
            throw new RegraNegocioException("Informe um Valor Válido.");
        }
        if(lancamento.getTipoDeLancamento() == TipoDeLancamento.RECEITA){
            //Atualiar as propriedades não necessárias no lançamento de Crédito
            lancamento.setFormaDePagamento(null);
            lancamento.setNumeroCheque(null);
            lancamento.setTipoDeDespesa(null);
            if(lancamento.getFormaDeRecebimentoCredito() == FormaDeRecebimentoCredito.CONTA_CORRENTE){
                lancamento.setCartaoDeCredito(null);
                lancamento.setFaturaCartao(null);
            }
        } else {
            lancamento.setFormaDeRecebimentoCredito(null);
        }

        if (lancamento.getFormaDePagamento() == FormaDePagamento.DEBITO || lancamento.getFormaDePagamento() == FormaDePagamento.PIX){
            lancamento.setCartaoDeCredito(null);
            lancamento.setNumeroCheque(null);
        }

    }

    public void criarParcelas(Lancamento lancamento)throws CloneNotSupportedException{
        for (int i=1; lancamento.getNumeroDeParcelas() > i; i++) {

            Lancamento parcelaLancamento = (Lancamento) lancamento.clone();
            BigDecimal valorParcela = BigDecimal.valueOf(lancamento.getValorLancamento().doubleValue()).divide(BigDecimal.valueOf(lancamento.getNumeroDeParcelas()), MathContext.DECIMAL128);
            lancamento.setValorParcelaLancamento(valorParcela);
            parcelaLancamento.setValorParcelaLancamento(valorParcela);
            parcelaLancamento.setParcela(lancamento);
            lancamento.adicionarParcela(parcelaLancamento);
        }
    }

    public void diminuirQuantidadeDeParcelas(Lancamento lancamento, int numeroReducao){
        lancamento.setNumeroDeParcelas(lancamento.getNumeroDeParcelas()-numeroReducao);
    }

    @Override
    public Optional<Lancamento> obterPorId(Long id) {
        return repositorio.findById(id);
    }

}
