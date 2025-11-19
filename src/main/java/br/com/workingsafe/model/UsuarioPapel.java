package br.com.workingsafe.model;

import jakarta.persistence.*;

@Entity
@Table(name = "T_GS_USUARIO_PAPEL",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_usuario_papel",
                columnNames = {"id_usuario", "id_papel"}
        )
)
public class UsuarioPapel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_papel")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // pode manter LAZY aqui
    @JoinColumn(name = "id_usuario",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_up_usuario"))
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER) // <-- MUDA PRA EAGER
    @JoinColumn(name = "id_papel",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_up_papel"))
    private Papel papel;

    // getters e setters

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

    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }
}
