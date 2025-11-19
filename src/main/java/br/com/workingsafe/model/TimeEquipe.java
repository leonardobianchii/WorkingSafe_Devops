package br.com.workingsafe.model;

import jakarta.persistence.*;

@Entity
@Table(name = "T_GS_TIME",
        uniqueConstraints = @UniqueConstraint(name = "uk_time_empresa_nome", columnNames = {"id_empresa", "nm_time"}))
public class TimeEquipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_time")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", foreignKey = @ForeignKey(name = "fk_time_empresa"))
    private Empresa empresa;

    @Column(name = "nm_time", nullable = false, length = 120)
    private String nome;

    @Column(name = "ds_time", length = 300)
    private String descricao;

    // -------------------------
    // Getters e Setters
    // -------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
