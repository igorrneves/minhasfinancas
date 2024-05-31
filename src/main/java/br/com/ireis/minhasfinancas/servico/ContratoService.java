package br.com.ireis.minhasfinancas.servico;

import br.com.ireis.minhasfinancas.modelo.Contrato;
import br.com.ireis.minhasfinancas.modelo.repositorio.ContratoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ContratoService {
    private ContratoRepository contratoRepository;

    public ContratoService(ContratoRepository contratoRepository) {
        super();
        this.contratoRepository = contratoRepository;
    }

    @Transactional
    public Contrato salvar(Contrato contrato) {
        return contratoRepository.saveAndFlush(contrato);
    }

    @Transactional
    public Contrato atualizar(Contrato contrato) {
        Objects.requireNonNull(contrato.getId());
        return contratoRepository.saveAndFlush(contrato);
    }

    @Transactional
    public void deletar(Contrato contrato) {
        Objects.requireNonNull(contrato.getId());
        contratoRepository.saveAndFlush(contrato);
    }

    public Optional<Contrato> obterPorId(long id){
        return contratoRepository.findById(id);
    }

    public List<Contrato> buscarTodos(){
        return contratoRepository.findAll();
    }
}
