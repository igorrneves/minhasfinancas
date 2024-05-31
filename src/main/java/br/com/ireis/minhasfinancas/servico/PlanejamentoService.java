package br.com.ireis.minhasfinancas.servico;

import br.com.ireis.minhasfinancas.modelo.FaturaCartao;
import br.com.ireis.minhasfinancas.modelo.Lancamento;
import br.com.ireis.minhasfinancas.modelo.Planejamento;
import br.com.ireis.minhasfinancas.modelo.PlanejamentoGrupoLancamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.FormaDePagamento;
import br.com.ireis.minhasfinancas.modelo.repositorio.PlanejamentoRepository;
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
public class PlanejamentoService {

    @Autowired
    private LancamentoService lancamentoService;
    @Autowired
    private FaturaCartaoService faturaCartaoService;

    private final PlanejamentoRepository planejamentoRepository;

    public PlanejamentoService(PlanejamentoRepository planejamentoRepository) {
        super();
        this.planejamentoRepository = planejamentoRepository;
    }

    @Transactional
    public Planejamento salvar(Planejamento planejamento) {
        validar(planejamento);
        return planejamentoRepository.saveAndFlush(planejamento);
    }

    @Transactional
    public Planejamento atualizar(Planejamento planejamento) {
        Objects.requireNonNull(planejamento.getId());
        return planejamentoRepository.save(planejamento);

    }

    public void deletar(Planejamento planejamento) {
        Objects.requireNonNull(planejamento.getId());
        planejamentoRepository.delete(planejamento);
    }

    public void deletarPorId(Long id) {
        Objects.requireNonNull(id);
        planejamentoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Planejamento> buscar(Planejamento planejamento) {

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id", "usuario")
                .withIncludeNullValues();

        Example<Planejamento> example = Example.of(planejamento, matcher);

        List<Planejamento> planejamentos = planejamentoRepository.findAll(example);

        return planejamentos;
    }

    @Transactional(readOnly = true)
    public List<Planejamento> buscarTodos() {
        List<Planejamento> planejamentos = planejamentoRepository.findAll(Sort.by(Sort.Direction.ASC, "mes"));
        return planejamentos;
    }

    public void validar(Planejamento planejamento) {


    }

    public Optional<Planejamento> obterPorId(Long id) {
        return planejamentoRepository.findById(id);
    }

    public List<PlanejamentoGrupoLancamento> calcularRealizadoMensal(int ano, int mes){
        Planejamento planejamento = buscarPorAnoMes(ano, mes);
        for (PlanejamentoGrupoLancamento grupoPlanejado : planejamento.getGruposPlanejados()) {
            BigDecimal valorTotalRealizadoPorGrupo = BigDecimal.valueOf(0);
            List<FaturaCartao> faturas = faturaCartaoService.buscarPorMesAnoGrupo(grupoPlanejado.getPlanejamento().getAno(), grupoPlanejado.getPlanejamento().getMes());
            List<Lancamento>  lancamentos = lancamentoService.buscarPorMesAnoEstadoGrupo(grupoPlanejado.getPlanejamento().getAno(), grupoPlanejado.getPlanejamento().getMes(), grupoPlanejado.getGrupoLancamento());
            for (Lancamento lancamento : lancamentos){
                valorTotalRealizadoPorGrupo = valorTotalRealizadoPorGrupo.add(lancamento.getValorLancamento());
                grupoPlanejado.getLancamentosRealizados().add(lancamento);
            }
            for (FaturaCartao faturaCartao : faturas){
                for (Lancamento lancamento: faturaCartao.getLancamentos()){
                    if(lancamento.getGrupoLancamento().getId() == grupoPlanejado.getGrupoLancamento().getId()) {
                        if(lancamento.getFormaDePagamento() != FormaDePagamento.DEBITO) {
                            if (lancamento.getNumeroDeParcelas() > 1) {
                                valorTotalRealizadoPorGrupo = valorTotalRealizadoPorGrupo.add(lancamento.getValorParcelaLancamento());
                            } else {
                                valorTotalRealizadoPorGrupo = valorTotalRealizadoPorGrupo.add(lancamento.getValorLancamento());
                            }
                            grupoPlanejado.getLancamentosRealizados().add(lancamento);
                        }
                    }
                }
            }
            grupoPlanejado.setValorRealizado(valorTotalRealizadoPorGrupo);
        }
        return planejamento.getGruposPlanejados();
    }

    public PlanejamentoGrupoLancamento calcularRealizadoMensalPorGrupo(int ano, int mes, long grupoId){
        List<PlanejamentoGrupoLancamento> planejamentoGrupoLancamento = calcularRealizadoMensal(ano,mes);
        PlanejamentoGrupoLancamento grupoLancamentos = new PlanejamentoGrupoLancamento();
        for (PlanejamentoGrupoLancamento grupoPlanejado : planejamentoGrupoLancamento) {
            if (grupoPlanejado.getId() == grupoId) {
                grupoLancamentos =  grupoPlanejado;
                break;
            }
        }
        return grupoLancamentos;
    }

    public Planejamento buscarPorAnoMes(int ano, int mes){
        return planejamentoRepository.buscarPorMesAno(ano,mes);
    }
}
