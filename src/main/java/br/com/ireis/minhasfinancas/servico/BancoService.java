package br.com.ireis.minhasfinancas.servico;

import br.com.ireis.minhasfinancas.excecoes.RegraNegocioException;
import br.com.ireis.minhasfinancas.modelo.Banco;
import br.com.ireis.minhasfinancas.modelo.repositorio.BancoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BancoService {
    private BancoRepository repositorio;

    public BancoService(BancoRepository repositorio) {
        super();
        this.repositorio = repositorio;
    }

    @Transactional
    public Banco salvar(Banco banco){
        validar(banco);
        return repositorio.saveAndFlush(banco);
    }
    @Transactional
    public Banco atualizar(Banco banco) {
        Objects.requireNonNull(banco.getId());
        validar(banco);
        return repositorio.saveAndFlush(banco);

    }

    public void deletarPorId(Long id) {
        Objects.requireNonNull(id);
        repositorio.deleteById(id);
    }

    public Optional<Banco> obterPorId(Long id) {
        return repositorio.findById(id);
    }

    public void validar(Banco banco){
        if(banco.getNome()==null || banco.getNome().trim().equals("")){
            throw new RegraNegocioException("Informe um nome válido para o banco.");
        }
        if(banco.getAgencia()==null || banco.getAgencia().trim().equals("")){
            throw new RegraNegocioException("Informe uma agência válida.");
        }
        if(banco.getConta()==null || banco.getConta().trim().equals("")){
            throw new RegraNegocioException("Informe uma conta válida.");
        }
    }

    public List<Banco> buscarTodosBancos() {
        List<Banco> bancos = repositorio.findAll();
        return bancos;
    }
}
