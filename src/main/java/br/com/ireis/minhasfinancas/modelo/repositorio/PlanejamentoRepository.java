package br.com.ireis.minhasfinancas.modelo.repositorio;

import br.com.ireis.minhasfinancas.modelo.Planejamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlanejamentoRepository extends JpaRepository<Planejamento,Long> {
    @Query("select planejamento from Planejamento planejamento where planejamento.ano = ?1 and planejamento.mes = ?2")
    Planejamento buscarPorMesAno(int ano, int mes);
}
