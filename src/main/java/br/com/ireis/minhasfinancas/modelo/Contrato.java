package br.com.ireis.minhasfinancas.modelo;

import br.com.ireis.minhasfinancas.modelo.enumeracao.SituacaoContrato;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contrato", schema="financas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Contrato {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id=null;
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate dataAbertura;
    private String descricao;
    private String numero;
    private BigDecimal valorMensalidade = BigDecimal.ZERO;
    @Enumerated(value = EnumType.STRING)
    private SituacaoContrato situacaoContrato = SituacaoContrato.VIGENTE;
    @ManyToOne
    @JoinColumn(name = "grupo_lancamento_id")
    private GrupoLancamento grupoLancamento;
    @OneToMany(targetEntity = Lancamento.class, mappedBy = "contrato", cascade=CascadeType.PERSIST)
    @Column(name = "contrato_id",  nullable = true)
    private List<Lancamento> lancamentos = new ArrayList<>();
}
