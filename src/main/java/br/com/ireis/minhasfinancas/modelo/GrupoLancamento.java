package br.com.ireis.minhasfinancas.modelo;

import br.com.ireis.minhasfinancas.modelo.enumeracao.TipoDeLancamento;
import br.com.ireis.minhasfinancas.modelo.registro.DadosCadastroGrupoLancamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "grupo_lancamento", schema="financas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GrupoLancamento {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id=null;
    private String nomeGrupo;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "tipo_lancamento")
    private TipoDeLancamento tipoDeLancamento = TipoDeLancamento.DESPESA;
    @Transient
    private BigDecimal valorPlanejado;

    public GrupoLancamento(DadosCadastroGrupoLancamento dadosCadastroGrupoLancamento) {
        this.id = dadosCadastroGrupoLancamento.id();
        this.nomeGrupo = dadosCadastroGrupoLancamento.nomeGrupo().toUpperCase();
        this.tipoDeLancamento = dadosCadastroGrupoLancamento.tipoDeLancamento();
    }
}
