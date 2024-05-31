package br.com.ireis.minhasfinancas.modelo;

import br.com.ireis.minhasfinancas.modelo.registro.DadosCadastroPlanejamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "planejamento", schema="financas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Planejamento {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id=null;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    private int mes;
    private int ano;

    @OneToMany(targetEntity = PlanejamentoGrupoLancamento.class, mappedBy = "planejamento", cascade = CascadeType.ALL)
    @Column(name = "planejamento_id")
    private List<PlanejamentoGrupoLancamento> gruposPlanejados = new ArrayList<>();

    @Transient
    private List<GrupoLancamento> grupoLancamentos;


    public Planejamento(DadosCadastroPlanejamento dadosCadastroPlanejamento) {
        this.id = dadosCadastroPlanejamento.id();
        //this.usuario = new Usuario(1l,"igor","igor@gmail.com","123", (Set<Role>) new Role("ROLE_ADMIN"));
        this.mes = dadosCadastroPlanejamento.mes();
        this.ano = dadosCadastroPlanejamento.ano();
    }
}
