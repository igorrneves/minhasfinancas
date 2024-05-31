package br.com.ireis.minhasfinancas.servico;

import br.com.ireis.minhasfinancas.modelo.Emprestimo;
import br.com.ireis.minhasfinancas.modelo.Investimento;
import br.com.ireis.minhasfinancas.modelo.Lancamento;
import br.com.ireis.minhasfinancas.modelo.repositorio.InvestimentoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class InvestimentoService {
    private InvestimentoRepository investimentoRepository;

    public InvestimentoService(InvestimentoRepository investimentoRepository) {
        super();
        this.investimentoRepository = investimentoRepository;
    }

    public Optional<Investimento> obterPorId(Long id) {
        return investimentoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Investimento> buscarTodos() {
        List<Investimento> investimentos = investimentoRepository.findAll(Sort.by(Sort.Direction.ASC, "titulo"));
        investimentos.forEach(i->i.setValorAplicado(i.getLancamentos().stream().map(Lancamento::getValorLancamento).reduce(BigDecimal.ZERO, BigDecimal::add)));
        return investimentos;
    }

    @Transactional
    public Investimento salvar(Investimento investimento){
        return investimentoRepository.saveAndFlush(investimento);
    }
}
