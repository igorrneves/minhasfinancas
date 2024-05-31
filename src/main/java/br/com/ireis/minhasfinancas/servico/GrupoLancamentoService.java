package br.com.ireis.minhasfinancas.servico;

import br.com.ireis.minhasfinancas.excecoes.RegraNegocioException;
import br.com.ireis.minhasfinancas.modelo.GrupoLancamento;
import br.com.ireis.minhasfinancas.modelo.repositorio.GrupoLancamentoRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Service
public class GrupoLancamentoService {

    private GrupoLancamentoRepository repositorio;

    public GrupoLancamentoService(GrupoLancamentoRepository repositorio) {
        super();
        this.repositorio = repositorio;
    }


    public Page<GrupoLancamento> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<GrupoLancamento> list;

        if (buscarTodos().size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, buscarTodos().size());
            list = buscarTodos().subList(startItem, toIndex);
        }

        Page<GrupoLancamento> bookPage
                = new PageImpl<GrupoLancamento>(list, PageRequest.of(currentPage, pageSize), buscarTodos().size());

        return bookPage;
    }

    @Transactional
    public GrupoLancamento salvar(GrupoLancamento grupoLancamento){
        validar(grupoLancamento);
        return repositorio.saveAndFlush(grupoLancamento);
    }
    @Transactional
    public GrupoLancamento atualizar(GrupoLancamento grupoLancamento) {
        Objects.requireNonNull(grupoLancamento.getId());
        validar(grupoLancamento);
        return repositorio.saveAndFlush(grupoLancamento);

    }

    public void deletarPorId(Long id) {
        Objects.requireNonNull(id);
        repositorio.deleteById(id);
    }

    public Optional<GrupoLancamento> obterPorId(Long id) {
        return repositorio.findById(id);
    }

    public void validar(GrupoLancamento grupoLancamento){
        if(grupoLancamento.getNomeGrupo()==null || grupoLancamento.getNomeGrupo().trim().equals("")){
            throw new RegraNegocioException("Informe um nome válido para o banco.");
        }
    }

    public List<GrupoLancamento> buscarTodos() {
        List<GrupoLancamento> grupos = repositorio.findAll(Sort.by(Sort.Direction.ASC, "nomeGrupo"));
        return grupos;
    }
}
