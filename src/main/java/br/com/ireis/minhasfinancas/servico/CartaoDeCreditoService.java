package br.com.ireis.minhasfinancas.servico;

import br.com.ireis.minhasfinancas.excecoes.RegraNegocioException;
import br.com.ireis.minhasfinancas.modelo.CartaoDeCredito;
import br.com.ireis.minhasfinancas.modelo.repositorio.CartaoDeCreditoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CartaoDeCreditoService {
    private CartaoDeCreditoRepository repositorio;

    public CartaoDeCreditoService(CartaoDeCreditoRepository repository) {
        super();
        this.repositorio = repository;
    }


    @Transactional
    public CartaoDeCredito salvar(CartaoDeCredito cartaoDeCredito){
        validar(cartaoDeCredito);
        return repositorio.saveAndFlush(cartaoDeCredito);
    }
    @Transactional
    public CartaoDeCredito atualizar(CartaoDeCredito cartaoDeCredito) {
        Objects.requireNonNull(cartaoDeCredito.getId());
        validar(cartaoDeCredito);
        return repositorio.saveAndFlush(cartaoDeCredito);

    }

    public void deletarPorId(Long id) {
        Objects.requireNonNull(id);
        repositorio.deleteById(id);
    }

    public Optional<CartaoDeCredito> obterPorId(Long id) {
        return repositorio.findById(id);
    }

    public void validar(CartaoDeCredito cartaoDeCredito){
        if(cartaoDeCredito.getNumero()==null || cartaoDeCredito.getNumero().trim().equals("")){
            throw new RegraNegocioException("Informe um número válido para o cartão de crédito.");
        }
    }

    public List<CartaoDeCredito> buscarTodos() {
        List<CartaoDeCredito> cartaoDeCredito = repositorio.findAll();
        return cartaoDeCredito;
    }


}
