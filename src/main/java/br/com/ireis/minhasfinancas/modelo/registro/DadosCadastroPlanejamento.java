package br.com.ireis.minhasfinancas.modelo.registro;

import br.com.ireis.minhasfinancas.modelo.GrupoLancamento;

import java.util.List;

public record DadosCadastroPlanejamento(
        Long id,
        int mes,
        int ano,
        List<GrupoLancamento> gruposLancamentos
) {
}
