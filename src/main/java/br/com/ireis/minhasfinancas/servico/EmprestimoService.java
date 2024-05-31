package br.com.ireis.minhasfinancas.servico;

import br.com.ireis.minhasfinancas.modelo.Emprestimo;
import br.com.ireis.minhasfinancas.modelo.repositorio.EmprestimoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmprestimoService {
    private EmprestimoRepository repositorio;

    public EmprestimoService(EmprestimoRepository repositorio) {
        super();
        this.repositorio = repositorio;
    }

    @Transactional
    public Emprestimo salvar(Emprestimo emprestimo){
        return repositorio.saveAndFlush(emprestimo);
    }

    @Transactional(readOnly = true)
    public List<Emprestimo> buscarTodos() {
        List<Emprestimo> emprestimos =  repositorio.findAll(Sort.by(Sort.Direction.ASC,"dataAbertura"));
        return emprestimos;
    }
    public Optional<Emprestimo> obterPorId(Long id) {
        return repositorio.findById(id);
    }
}
