package br.com.workingsafe.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "T_GS_USUARIO",
        uniqueConstraints = @UniqueConstraint(name = "uk_usuario_email", columnNames = "email"))
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", foreignKey = @ForeignKey(name = "fk_usuario_empresa"))
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_time", foreignKey = @ForeignKey(name = "fk_usuario_time"))
    private TimeEquipe time;

    @Column(name = "nm_usuario", nullable = false, length = 150)
    private String nome;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "fl_ativo", length = 1)
    private String ativo = "S";   // 'S' ou 'N'

    @Column(name = "fuso_horario", length = 60)
    private String fusoHorario;

    @Column(name = "dt_cadastro")
    private LocalDateTime dtCadastro = LocalDateTime.now();

    // getters e setters

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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }

    public String getFusoHorario() {
        return fusoHorario;
    }

    public void setFusoHorario(String fusoHorario) {
        this.fusoHorario = fusoHorario;
    }

    public LocalDateTime getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(LocalDateTime dtCadastro) {
        this.dtCadastro = dtCadastro;
    }
}
