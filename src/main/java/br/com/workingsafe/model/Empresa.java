package br.com.workingsafe.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "T_GS_EMPRESA",
        uniqueConstraints = @UniqueConstraint(name = "uk_empresa_nome", columnNames = "nm_empresa"))
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private Long id;

    @Column(name = "nm_empresa", nullable = false, length = 150)
    private String nome;

    @Column(name = "cnpj", length = 18)
    private String cnpj;

    @Column(name = "email_contato", length = 150)
    private String emailContato;

    @Column(name = "dt_cadastro")
    private LocalDateTime dtCadastro = LocalDateTime.now();

    // -------------------------
    // Getters e Setters
    // -------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEmailContato() {
        return emailContato;
    }

    public void setEmailContato(String emailContato) {
        this.emailContato = emailContato;
    }

    public LocalDateTime getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(LocalDateTime dtCadastro) {
        this.dtCadastro = dtCadastro;
    }
}
