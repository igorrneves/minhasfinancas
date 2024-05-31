package br.com.ireis.minhasfinancas.modelo.repositorio;

import br.com.ireis.minhasfinancas.modelo.Lancamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.EstadoDoLancamento;
import br.com.ireis.minhasfinancas.modelo.enumeracao.TipoDeLancamento;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LancamentoSpecification implements Specification<Lancamento> {
    private SearchCriteria criteria;
    public static final String DATA_LANCAMENTO = "dataDoLancamento";
    public static final String TIPO_LANCAMENTO = "tipoDeLancamento";
    public static final String ESTADO_LANCAMENTO = "estadoDoLancamento";

    @Override
    public Predicate toPredicate(Root<Lancamento> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if(criteria.getValue()!=null){
            if (criteria.getOperation().equalsIgnoreCase(">")) {
                return builder.greaterThanOrEqualTo(
                        root.<String> get(criteria.getKey()), criteria.getValue().toString());
            }
            else if (criteria.getOperation().equalsIgnoreCase("<")) {
                return builder.lessThanOrEqualTo(
                        root.<String> get(criteria.getKey()), criteria.getValue().toString());
            }
            else if (criteria.getOperation().equalsIgnoreCase(":")) {
                if (root.get(criteria.getKey()).getJavaType() == String.class) {
                    return builder.like(
                            root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
                } if (root.get(criteria.getKey()).getJavaType() == LocalDate.class) {
                    LocalDate date = (LocalDate) criteria.getValue();
                    return builder.greaterThan(root.get(criteria.getKey()), date);
                }else {
                    return builder.equal(root.get(criteria.getKey()), criteria.getValue());
                }
            }
        } else {
            return builder.conjunction();
        }
        return null;
    }
    public static Specification<Lancamento> filterBy(Lancamento lancamentoFiltro) {
        return Specification
                .where(hasDataDoLancamento(lancamentoFiltro.getDataDoLancamento()))
                .and(hasTipoDeLancamento(lancamentoFiltro.getTipoDeLancamento()))
                .and(hasEstadoDoLancamento(lancamentoFiltro.getEstadoDoLancamento()));
    }

    private static Specification<Lancamento> hasDataDoLancamento(LocalDate dataDoLancamento) {
      return ((root, query, cb) -> dataDoLancamento == null  ? cb.conjunction() : cb.greaterThan(root.get(DATA_LANCAMENTO), dataDoLancamento));

    }

    private static Specification<Lancamento> hasTipoDeLancamento(TipoDeLancamento tipoDeLancamento) {
        return ((root, query, cb) -> tipoDeLancamento == null  ? cb.conjunction() : cb.equal(root.get(TIPO_LANCAMENTO), tipoDeLancamento));

    }
    private static Specification<Lancamento> hasEstadoDoLancamento(EstadoDoLancamento estadoDoLancamento) {
        return ((root, query, cb) -> estadoDoLancamento == null ? cb.conjunction() : cb.equal(root.get(ESTADO_LANCAMENTO), estadoDoLancamento));
    }
}
