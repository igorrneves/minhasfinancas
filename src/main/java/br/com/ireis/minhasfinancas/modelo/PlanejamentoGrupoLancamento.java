package br.com.ireis.minhasfinancas.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "planejamento_grupo_lancamento", schema="financas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PlanejamentoGrupoLancamento {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id=null;

    @ManyToOne
    @JoinColumn(name = "planejamento_id")
    private Planejamento planejamento;

    @ManyToOne
    @JoinColumn(name = "grupo_lancamento_id")
    @OrderBy(value = "nomeGrupo ASC")
    private GrupoLancamento grupoLancamento;

    private BigDecimal valorPlanejado;

    @Transient
    private BigDecimal valorRealizado;
    @Transient
    private List<Lancamento> lancamentosRealizados = new ArrayList<>();
}
