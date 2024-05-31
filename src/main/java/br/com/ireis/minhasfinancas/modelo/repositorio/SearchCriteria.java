package br.com.ireis.minhasfinancas.modelo.repositorio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SearchCriteria {
    private String key;
    private String operation;
    private Object value;

}
