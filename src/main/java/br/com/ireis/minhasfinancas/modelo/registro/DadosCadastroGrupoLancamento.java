package br.com.ireis.minhasfinancas.modelo.registro;

import br.com.ireis.minhasfinancas.modelo.enumeracao.TipoDeLancamento;

import java.math.BigDecimal;

public record DadosCadastroGrupoLancamento(
        Long id,
        String nomeGrupo,
        BigDecimal valorPlanejado,
        TipoDeLancamento tipoDeLancamento
) {
}
