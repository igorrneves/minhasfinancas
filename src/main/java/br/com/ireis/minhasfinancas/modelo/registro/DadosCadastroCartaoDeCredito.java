package br.com.ireis.minhasfinancas.modelo.registro;

import br.com.ireis.minhasfinancas.modelo.Banco;
import br.com.ireis.minhasfinancas.modelo.Usuario;
import br.com.ireis.minhasfinancas.modelo.enumeracao.BandeiraCartaoDeCredito;
import br.com.ireis.minhasfinancas.modelo.enumeracao.SituacaoCartaoDeCredito;

public record DadosCadastroCartaoDeCredito(

        Long id,
        Banco banco,
        Usuario usuario,
        BandeiraCartaoDeCredito bandeira,
        String numero,
        int mesValidade,
        int anoValidade,
        int codigoSeguranca,
        SituacaoCartaoDeCredito situacao,
        int diaVencimento
) {
}
