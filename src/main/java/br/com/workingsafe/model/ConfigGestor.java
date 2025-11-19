package br.com.workingsafe.model;

import jakarta.persistence.*;

@Entity
@Table(name = "T_GS_CONFIG_GESTOR")
public class ConfigGestor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_config_gestor")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", foreignKey = @ForeignKey(name = "fk_cfg_empresa"))
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_time", foreignKey = @ForeignKey(name = "fk_cfg_time"))
    private TimeEquipe time;

    @Column(name = "limiar_alerta", nullable = false)
    private Double limiarAlerta = 0.60;

    @Column(name = "janela_dias", nullable = false)
    private Integer janelaDias = 7;

    @Column(name = "fl_anonimizado", nullable = false, length = 1)
    private String anonimizado = "S"; // 'S' ou 'N'

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

    public Double getLimiarAlerta() {
        return limiarAlerta;
    }

    public void setLimiarAlerta(Double limiarAlerta) {
        this.limiarAlerta = limiarAlerta;
    }

    public Integer getJanelaDias() {
        return janelaDias;
    }

    public void setJanelaDias(Integer janelaDias) {
        this.janelaDias = janelaDias;
    }

    public String getAnonimizado() {
        return anonimizado;
    }

    public void setAnonimizado(String anonimizado) {
        this.anonimizado = anonimizado;
    }
}
