package br.com.ireis.minhasfinancas.modelo;

import jakarta.persistence.*;

@Entity
@Table(name="roles", schema="financas")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    public Role() {
    }
    public Role(String name) { this.name = name;  }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
