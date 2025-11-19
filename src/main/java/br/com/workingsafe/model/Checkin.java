package br.com.workingsafe.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "T_GS_CHECKIN")
public class Checkin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_checkin")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", foreignKey = @ForeignKey(name = "fk_checkin_usuario"))
    private Usuario usuario;

    @Column(name = "dt_checkin", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "vl_humor", nullable = false)
    private Integer humor; // 1..5

    @Column(name = "vl_foco", nullable = false)
    private Integer foco;  // 1..5

    @Column(name = "minutos_pausas")
    private Integer minutosPausas;

    @Column(name = "horas_trabalhadas")
    private Double horasTrabalhadas;

    @Lob
    @Column(name = "ds_observacoes")
    private String observacoes;

    @Column(name = "tags", length = 200)
    private String tags;

    @Column(name = "origem", length = 20)
    private String origem = "MOBILE";

    // -------------------------
    // Getters e Setters
    // -------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Integer getHumor() {
        return humor;
    }

    public void setHumor(Integer humor) {
        this.humor = humor;
    }

    public Integer getFoco() {
        return foco;
    }

    public void setFoco(Integer foco) {
        this.foco = foco;
    }

    public Integer getMinutosPausas() {
        return minutosPausas;
    }

    public void setMinutosPausas(Integer minutosPausas) {
        this.minutosPausas = minutosPausas;
    }

    public Double getHorasTrabalhadas() {
        return horasTrabalhadas;
    }

    public void setHorasTrabalhadas(Double horasTrabalhadas) {
        this.horasTrabalhadas = horasTrabalhadas;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }
}
