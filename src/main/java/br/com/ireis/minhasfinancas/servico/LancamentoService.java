package br.com.ireis.minhasfinancas.servico;

import br.com.ireis.minhasfinancas.excecoes.RegraNegocioException;
import br.com.ireis.minhasfinancas.modelo.FaturaCartao;
import br.com.ireis.minhasfinancas.modelo.GrupoLancamento;
import br.com.ireis.minhasfinancas.modelo.Lancamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.*;
import br.com.ireis.minhasfinancas.modelo.repositorio.LancamentoRepository;
import br.com.ireis.minhasfinancas.modelo.repositorio.LancamentoSpecification;
import br.com.ireis.minhasfinancas.modelo.repositorio.SearchCriteria;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.*;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.endsWith;

@Service
public class LancamentoService {
    @Autowired
    private FaturaCartaoService faturaCartaoService;
    private LancamentoRepository repositorio;

    public LancamentoService(LancamentoRepository repositorio) {
        super();
        this.repositorio = repositorio;
    }

    @Transactional
    public Lancamento salvar(Lancamento lancamento) throws CloneNotSupportedException {
        validar(lancamento);
        List<Lancamento> lancamentos = new ArrayList<>();
        if (lancamento.getNumeroDeRepeticaoDoLancamento() > 0) { //REPETIÇÃO DO LANÇAMENTO
            salvarLancamentoRepeticao(lancamento);
        } else if (lancamento.getNumeroDeParcelas() > 1 && lancamento.getFormaDePagamento() == FormaDePagamento.CREDITO) { // CRIAÇÃO PARCELAS
            for (int i = 1; lancamento.getNumeroDeParcelas() >= i; i++) {
                Lancamento parcelaLancamento = carregaClone(lancamento);
                if (i == 1) { // na primeira passada pelo loop, precisa atualizar somente a fatura do lançamento
                    this.associaLancamentoNaFatura(lancamento);
                    lancamento.setNumeroDaParcela(i);
                } else {
                    parcelaLancamento.setParcela(lancamento);
                    parcelaLancamento.setNumeroDaParcela(i);
                    this.associaLancamentoNaFatura(parcelaLancamento);
                    lancamentos.add(parcelaLancamento);
                }
            }
            lancamento.setParcelas(lancamentos);

        } else if (lancamento.getNumeroDeParcelas() == 1 && lancamento.getFormaDePagamento() == FormaDePagamento.CREDITO) { // COMPRA A VISTA NO CRÉDITO
            this.associaLancamentoNaFatura(lancamento);
        }
        if (lancamento.getTipoDeLancamento() == TipoDeLancamento.PAGAMENTO_FATURA_CARTAO){
            if (lancamento.getFaturaCartao().getValorPagoFatura().compareTo(lancamento.getFaturaCartao().getValorTotalFatura()) < 0){
                lancamento.getFaturaCartao().setEstado(EstadoDaFaturaCartaoDeCredito.PARCIALMENTE_PAGA);
            } else if (lancamento.getFaturaCartao().getValorPagoFatura().compareTo(lancamento.getFaturaCartao().getValorTotalFatura()) == 0){
                lancamento.getFaturaCartao().setEstado(EstadoDaFaturaCartaoDeCredito.FECHADA);
            }
            faturaCartaoService.atualizar(lancamento.getFaturaCartao());
            lancamento.setFaturaCartao(null);
        }
        return repositorio.saveAndFlush(lancamento);
    }

    public void associaLancamentoNaFatura(Lancamento lancamento) {

        LocalDate dataDeCorteDoCartao = LocalDate.of(
                lancamento.getDataDoLancamento().getYear(),
                lancamento.getDataDoLancamento().getMonthValue(),
                lancamento.getCartaoDeCredito().getDiaVencimento());


        if (lancamento.getDataDoLancamento().isAfter(dataDeCorteDoCartao.minusDays(10))) {
            //precisa jogar para a próxima fatura
            dataDeCorteDoCartao = dataDeCorteDoCartao.plusMonths(lancamento.getNumeroDaParcela());
        }

        FaturaCartao faturaCartao = new FaturaCartao();

        if (!faturaCartaoService.buscarPorCartaoMesAno(lancamento.getCartaoDeCredito(), dataDeCorteDoCartao.getYear(), dataDeCorteDoCartao.getMonthValue()).isPresent()) {
            faturaCartao.setCartaoDeCredito(lancamento.getCartaoDeCredito());
            faturaCartao.setMes(dataDeCorteDoCartao.getMonthValue());
            faturaCartao.setAno(dataDeCorteDoCartao.getYear());
            if (lancamento.getNumeroDeParcelas()>1){
                faturaCartao.setValorTotalFatura(lancamento.getValorParcelaLancamento());
            }else {
                faturaCartao.setValorTotalFatura(lancamento.getValorLancamento());
            }
        } else {
            faturaCartao = faturaCartaoService.buscarPorCartaoMesAno(lancamento.getCartaoDeCredito(), dataDeCorteDoCartao.getYear(), dataDeCorteDoCartao.getMonthValue()).get();
            if (lancamento.getNumeroDeParcelas()>1){
                faturaCartao.setValorTotalFatura(lancamento.getValorParcelaLancamento().add(faturaCartao.getValorTotalFatura()));
            } else {
                faturaCartao.setValorTotalFatura(lancamento.getValorLancamento().add(faturaCartao.getValorTotalFatura()));
            }
        }
        lancamento.setFaturaCartao(faturaCartao);
    }

    public void salvarLancamentoRepeticao(Lancamento lancamento) throws CloneNotSupportedException {
        for (int i = 1; lancamento.getNumeroDeRepeticaoDoLancamento() > i; i++) {
            Lancamento repeticaoLancamento = (Lancamento) lancamento.clone();
            LocalDate dataDoLancamento = repeticaoLancamento.getDataDoLancamento();
            repeticaoLancamento.setDataDoLancamento(dataDoLancamento.plusMonths(i));
            repositorio.saveAndFlush(repeticaoLancamento);
        }
    }

    public Lancamento carregaClone(Lancamento lancamento) {
        Lancamento clone = new Lancamento();
        clone.setDataDoLancamento(lancamento.getDataDoLancamento());
        clone.setUsuario(lancamento.getUsuario());
        clone.setDescricaoLancamento(lancamento.getDescricaoLancamento());
        clone.setValorLancamento(lancamento.getValorLancamento());
        clone.setValorParcelaLancamento(lancamento.getValorParcelaLancamento());
        clone.setNumeroDeParcelas(lancamento.getNumeroDeParcelas());
        clone.setTipoDeLancamento(lancamento.getTipoDeLancamento());
        clone.setTipoDeDespesa(lancamento.getTipoDeDespesa());
        clone.setFormaDePagamento(lancamento.getFormaDePagamento());
        clone.setFormaDeRecebimentoCredito(lancamento.getFormaDeRecebimentoCredito());
        clone.setGrupoLancamento(lancamento.getGrupoLancamento());
        clone.setCartaoDeCredito(lancamento.getCartaoDeCredito());
        clone.setNumeroCheque(lancamento.getNumeroCheque());
        clone.setBanco(lancamento.getBanco());
        return clone;
    }

    @Transactional
    public Lancamento atualizar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        validar(lancamento);
        return repositorio.save(lancamento);

    }

    public void deletar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        repositorio.delete(lancamento);
    }

    public void deletarPorId(Long id) {
        Objects.requireNonNull(id);
        Lancamento lancamento = this.obterPorId(id).get();
        if (lancamento.getFaturaCartao() != null ){
            if (lancamento.getNumeroDeParcelas()>1){
                lancamento.getFaturaCartao().setValorTotalFatura(lancamento.getFaturaCartao().getValorTotalFatura().subtract(lancamento.getValorParcelaLancamento()));
            }else{
                lancamento.getFaturaCartao().setValorTotalFatura(lancamento.getFaturaCartao().getValorTotalFatura().subtract(lancamento.getValorLancamento()));
            }

        }
        repositorio.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id", "usuario", "valorParcelaLancamento", "numeroDeParcelas", "tipoDeDespesa", "formaDePagamento", "banco", "formaDeRecebimentoCredito",
                        "cartaoDeCredito", "numeroCheque", "parcela,parcelas", "faturaCartao", "grupoLancamento", "valorLancamento",
                        "descricaoLancamento", "tipoDeLancamento")
                .withIgnoreNullValues();
        if (lancamentoFiltro.getDataDoLancamento() == null) {
            matcher.withIgnorePaths("dataDoLancamento");
        }

        Example<Lancamento> example = Example.of(lancamentoFiltro, matcher);

        List<Lancamento> lancamentos = repositorio.findAll(example, Sort.by(Sort.Direction.ASC, "dataDoLancamento"));

        return lancamentos;
    }

    @Transactional(readOnly = true)
    public List<Lancamento> buscarPorMesAnoEstadoGrupo(int ano, int mes, GrupoLancamento grupoLancamento) {
        return repositorio.buscarPorMesAnoEstadoGrupo(ano, mes, grupoLancamento);
    }

    @Transactional(readOnly = true)
    public BigDecimal buscarTotalPorTipoMesAno(int ano, int mes, TipoDeLancamento tipo) {
        return repositorio.buscarTotalPorTipoMesAno(ano, mes, tipo);
    }

    @Transactional(readOnly = true)
    public BigDecimal buscarTotalPorGrupoMesAno(int ano, int mes, GrupoLancamento grupoLancamento) {
        return repositorio.buscarTotalPorGrupoMesAno(ano, mes, grupoLancamento);
    }

    @Transactional(readOnly = true)
    public List<Lancamento> buscarPorMesAno(int ano, int mes) {
        return repositorio.buscarPorMesAno(ano, mes);
    }

    @Transactional(readOnly = true)
    public List<Lancamento> buscaFiltrada(Lancamento lancamentoFiltro) {
        LancamentoSpecification dataDoLancamentoSpecification = new LancamentoSpecification(new SearchCriteria("dataDoLancamento", ":", lancamentoFiltro.getDataDoLancamento()));
        LancamentoSpecification tipoDeLancamentoSpecification = new LancamentoSpecification(new SearchCriteria("tipoDeLancamento", ":", lancamentoFiltro.getTipoDeLancamento()));
        LancamentoSpecification estadoDoLancamentoSpecification = new LancamentoSpecification(new SearchCriteria("estadoDoLancamento", ":", lancamentoFiltro.getEstadoDoLancamento()));
        LancamentoSpecification bancoSpecification = new LancamentoSpecification(new SearchCriteria("banco", ":", lancamentoFiltro.getBanco()));
        LancamentoSpecification grupoLancamentoSpecification = new LancamentoSpecification(new SearchCriteria("grupoLancamento", ":", lancamentoFiltro.getGrupoLancamento()));
        LancamentoSpecification cartaoDeCreditoSpecification = new LancamentoSpecification(new SearchCriteria("cartaoDeCredito", ":", lancamentoFiltro.getCartaoDeCredito()));

        List<Lancamento> lancamentos = repositorio.findAll(Specification
                        .where(tipoDeLancamentoSpecification)
                        .and(estadoDoLancamentoSpecification)
                        .and(dataDoLancamentoSpecification)
                        .and(bancoSpecification)
                        .and(grupoLancamentoSpecification)
                        .and(cartaoDeCreditoSpecification),
                Sort.by(Sort.Direction.ASC, "dataDoLancamento"));
        return lancamentos;
    }

    @Transactional(readOnly = true)
    public List<Lancamento> buscarTodos() {
        List<Lancamento> lancamentos = repositorio.findAll(Sort.by(Sort.Direction.ASC, "tipoInvestimento"));
        return lancamentos;
    }

    @Transactional(readOnly = true)
    public List<Lancamento> buscarPorEstadoLancamento(EstadoDoLancamento estadoDoLancamento) {
        List<Lancamento> lancamentos = repositorio.buscarPorEstadoLancamento(estadoDoLancamento);
        return lancamentos;
    }

    public void validar(Lancamento lancamento) {
        if (lancamento.getDescricaoLancamento() == null || lancamento.getDescricaoLancamento().trim().equals("")) {
            throw new RegraNegocioException("Informe uma Descrição válida.");
        }
        if (lancamento.getDataDoLancamento() == null) {
            throw new RegraNegocioException("Informe uma Data válida.");
        }
        if (lancamento.getNumeroDeParcelas() < 1) {
            throw new RegraNegocioException("Informe a quantidade de parcelas.");
        } else if (lancamento.getNumeroDeParcelas() > 1 && lancamento.getId() == null) {
            try {
                criarParcelas(lancamento);
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }else if (lancamento.getNumeroDeParcelas() > 1 && lancamento.getId() != null){
            this.verificaSeHouveAlteracaoDaFatura(lancamento);
        }
        if (lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
            //lancamento.setUsuario(new Usuario(1l,"igor","igor@gmail.com","123", (Set<Role>) new Role("ROLE_ADMIN")));
        }
        if (lancamento.getValorLancamento() == null || lancamento.getValorLancamento().compareTo(BigDecimal.ZERO) < 1) {
            throw new RegraNegocioException("Informe um Valor Válido.");
        }
        if (lancamento.getTipoDeLancamento() == TipoDeLancamento.RECEITA) {
            //Atualiar as propriedades não necessárias no lançamento de Crédito
            ajustaPropriedadesLancamentoReceita(lancamento);
        } else if (lancamento.getTipoDeLancamento() == TipoDeLancamento.DESPESA) {
            ajustaPropriedadesLancamentoDespesa(lancamento);
        } else if (lancamento.getTipoDeLancamento() == TipoDeLancamento.ESTORNO_CARTAO){
            ajustaPropriedadesLancamentoEstorno(lancamento);
        }
    }

    public void verificaSeHouveAlteracaoDaFatura(Lancamento lancamento){
        if ((lancamento.getTipoDeLancamento() == TipoDeLancamento.DESPESA) && (lancamento.getFormaDePagamento()==FormaDePagamento.CREDITO)) {
            if (lancamento.getFaturaCartao() != null) {
                int mes = Integer.parseInt(lancamento.getFatura().substring(0,1));
                int ano = Integer.parseInt(lancamento.getFatura().substring(2,6));
                if ((mes != lancamento.getFaturaCartao().getMes()) || (ano != lancamento.getFaturaCartao().getAno())){
                    lancamento.setFaturaCartao(faturaCartaoService.buscarPorCartaoMesAno(lancamento.getCartaoDeCredito(),ano,mes).get());
                }
            }
        }
    }
    public void ajustaPropriedadesLancamentoReceita(Lancamento lancamento) {
        lancamento.setFormaDePagamento(null);
        lancamento.setNumeroCheque(null);
        lancamento.setTipoDeDespesa(null);
        if (lancamento.getFormaDeRecebimentoCredito() == FormaDeRecebimentoCredito.CONTA_CORRENTE) {
            lancamento.setCartaoDeCredito(null);
            lancamento.setFaturaCartao(null);
        }
    }

    public void ajustaPropriedadesLancamentoDespesa(Lancamento lancamento) {
        lancamento.setFormaDeRecebimentoCredito(null);
        if (lancamento.getTipoDeLancamento() != TipoDeLancamento.PAGAMENTO_FATURA_CARTAO) {
            if (lancamento.getFormaDePagamento() == FormaDePagamento.DEBITO || lancamento.getFormaDePagamento() == FormaDePagamento.PIX) {
                lancamento.setCartaoDeCredito(null);
                lancamento.setFaturaCartao(null);
                lancamento.setNumeroCheque(null);
            }
        }
    }

    public void ajustaPropriedadesLancamentoEstorno(Lancamento lancamento) {
        lancamento.setNumeroCheque(null);
        lancamento.setFormaDeRecebimentoCredito(null);
        if (lancamento.getFormaDePagamento() == FormaDePagamento.DEBITO || lancamento.getFormaDePagamento() == FormaDePagamento.PIX) {
            lancamento.setCartaoDeCredito(null);
            lancamento.setNumeroCheque(null);
        }
    }

    public void criarParcelas(Lancamento lancamento) throws CloneNotSupportedException {
        for (int i = 1; lancamento.getNumeroDeParcelas() > i; i++) {

            Lancamento parcelaLancamento = (Lancamento) lancamento.clone();
            BigDecimal valorParcela = BigDecimal.valueOf(lancamento.getValorLancamento().doubleValue()).divide(BigDecimal.valueOf(lancamento.getNumeroDeParcelas()), MathContext.DECIMAL128);
            lancamento.setValorParcelaLancamento(valorParcela);
            parcelaLancamento.setValorParcelaLancamento(valorParcela);
            parcelaLancamento.setParcela(lancamento);
            ajustaPropriedadesLancamentoDespesa(parcelaLancamento);
            lancamento.adicionarParcela(parcelaLancamento);
        }
    }

    public void diminuirQuantidadeDeParcelas(Lancamento lancamento, int numeroReducao) {
        lancamento.setNumeroDeParcelas(lancamento.getNumeroDeParcelas() - numeroReducao);
    }

    public Optional<Lancamento> obterPorId(Long id) {
        return repositorio.findById(id);
    }

    public List<Lancamento> buscarPorCartaoDeCreditoEstado(Lancamento lancamento) {
        return repositorio.buscarPorCartaoDeCreditoEstado(lancamento.getCartaoDeCredito(), lancamento.getEstadoDoLancamento());
    }

    public void ajustaPropriedadesLancamentoInvestimento(Lancamento lancamento) {
        lancamento.setDescricaoLancamento("Aplicação: "+lancamento.getInvestimento().getTitulo());
        lancamento.setBanco(lancamento.getInvestimento().getBanco());
        lancamento.setTipoDeLancamento(TipoDeLancamento.INVESTIMENTO);
        lancamento.setEstadoDoLancamento(EstadoDoLancamento.VALIDADO);
        lancamento.setNumeroDeParcelas(1);
        lancamento.setFormaDePagamento(FormaDePagamento.DEBITO);
        lancamento.setCartaoDeCredito(null);
        lancamento.setNumeroCheque(null);
        lancamento.setFormaDeRecebimentoCredito(null);
    }

    public void ajustaPropriedadesLancamentoEmprestimo(Lancamento lancamento) {
        lancamento.setDescricaoLancamento("PARCELA EMPRESTIMO "+lancamento.getEmprestimo().getBanco().getNome() +" / CONTRATO: "+lancamento.getEmprestimo().getContrato());
        lancamento.setBanco(lancamento.getEmprestimo().getBanco());
        lancamento.setTipoDeLancamento(TipoDeLancamento.DESPESA);
        lancamento.setEstadoDoLancamento(EstadoDoLancamento.VALIDADO);
        lancamento.setNumeroDeParcelas(1);
        lancamento.setFormaDePagamento(FormaDePagamento.DEBITO);
        lancamento.setCartaoDeCredito(null);
        lancamento.setNumeroCheque(null);
        lancamento.setFormaDeRecebimentoCredito(null);
    }

    public void ajustaPropriedadesLancamentoContrato(Lancamento lancamento) {
        lancamento.setDescricaoLancamento("CONTRATO: "+lancamento.getContrato().getDescricao());
        lancamento.setGrupoLancamento(lancamento.getContrato().getGrupoLancamento());
        lancamento.setTipoDeLancamento(TipoDeLancamento.DESPESA);
        lancamento.setEstadoDoLancamento(EstadoDoLancamento.VALIDADO);
        lancamento.setNumeroDeParcelas(1);
        lancamento.setFormaDeRecebimentoCredito(null);
    }

    public void ajustaPropriedadesLancamentoPagamentoFaturaCartao(Lancamento lancamento) {
        lancamento.setDescricaoLancamento(  lancamento.getCartaoDeCredito().getBandeira()+
                                            " / NUMERO: "+lancamento.getCartaoDeCredito().getNumero()+
                                            " / FATURA: "+lancamento.getFaturaCartao().getMes()+"/"+lancamento.getFaturaCartao().getAno());
        lancamento.setTipoDeLancamento(TipoDeLancamento.PAGAMENTO_FATURA_CARTAO);
        lancamento.setEstadoDoLancamento(EstadoDoLancamento.VALIDADO);
        lancamento.setNumeroDeParcelas(1);
        lancamento.setNumeroDaParcela(0);
        lancamento.setFormaDeRecebimentoCredito(null);
    }
}
