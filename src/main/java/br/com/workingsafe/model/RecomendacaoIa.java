package br.com.workingsafe.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "T_GS_RECOMENDACAO_IA")
public class RecomendacaoIa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recomendacao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_usuario",
            foreignKey = @ForeignKey(name = "fk_recomendacao_usuario")
    )
    private Usuario usuario;

    @Column(name = "categoria", length = 50, nullable = false)
    private String categoria;

    @Column(name = "descricao", length = 1000, nullable = false)
    private String descricao;

    @Column(name = "dt_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "dt_validade", nullable = false)
    private LocalDate dataValidade;

    @Column(name = "origem", length = 30)
    private String origem;

    public RecomendacaoIa() {}

    // getters / setters ...

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }

    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getCategoria() { return categoria; }

    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescricao() { return descricao; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }

    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDate getDataValidade() { return dataValidade; }

    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }

    public String getOrigem() { return origem; }

    public void setOrigem(String origem) { this.origem = origem; }
}
