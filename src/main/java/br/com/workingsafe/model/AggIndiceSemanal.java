package br.com.workingsafe.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "T_GS_AGG_INDICE_SEMANAL",
        uniqueConstraints = @UniqueConstraint(name = "uk_agg_scope_semana",
                columnNames = {"id_empresa", "id_time", "dt_inicio_semana"}))
public class AggIndiceSemanal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agg")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", foreignKey = @ForeignKey(name = "fk_agg_empresa"))
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_time", foreignKey = @ForeignKey(name = "fk_agg_time"))
    private TimeEquipe time;

    @Column(name = "dt_inicio_semana", nullable = false)
    private LocalDate inicioSemana;

    @Column(name = "qtd_usuarios", nullable = false)
    private Integer qtdUsuarios;

    @Column(name = "media_indice", nullable = false)
    private Double mediaIndice;

    @Column(name = "media_risco", nullable = false)
    private Double mediaRisco;

    @Column(name = "dt_geracao")
    private LocalDateTime dtGeracao = LocalDateTime.now();

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

    public TimeEquipe getTime() {
        return time;
    }

    public void setTime(TimeEquipe time) {
        this.time = time;
    }

    public LocalDate getInicioSemana() {
        return inicioSemana;
    }

    public void setInicioSemana(LocalDate inicioSemana) {
        this.inicioSemana = inicioSemana;
    }

    public Integer getQtdUsuarios() {
        return qtdUsuarios;
    }

    public void setQtdUsuarios(Integer qtdUsuarios) {
        this.qtdUsuarios = qtdUsuarios;
    }

    public Double getMediaIndice() {
        return mediaIndice;
    }

    public void setMediaIndice(Double mediaIndice) {
        this.mediaIndice = mediaIndice;
    }

    public Double getMediaRisco() {
        return mediaRisco;
    }

    public void setMediaRisco(Double mediaRisco) {
        this.mediaRisco = mediaRisco;
    }

    public LocalDateTime getDtGeracao() {
        return dtGeracao;
    }

    public void setDtGeracao(LocalDateTime dtGeracao) {
        this.dtGeracao = dtGeracao;
    }
}
