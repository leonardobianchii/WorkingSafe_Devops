package br.com.workingsafe.model;

import jakarta.persistence.*;

@Entity
@Table(name = "T_GS_PAPEL",
        uniqueConstraints = @UniqueConstraint(name = "uk_papel_codigo", columnNames = "cd_papel"))
public class Papel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_papel")
    private Long id;

    @Column(name = "cd_papel", nullable = false, length = 30)
    private String codigo; // COLABORADOR, GESTOR, ADMIN

    @Column(name = "ds_papel", length = 150)
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
