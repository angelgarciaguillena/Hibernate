package entities;

import javax.persistence.*;

@Entity
@Table(name = "Likes")
public class Like {
	
	//Atributos
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idLikes")
    private Integer idLike;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idUsuarios", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idPosts", nullable = false)
    private Post post;
    
    
    //Constructores
    public Like() {
    }
    
    public Like(Usuario usuario, Post post) {
        this.usuario = usuario;
        this.post = post;
    }
    
    
    // Getters y Setters
    public Integer getIdLike() {
        return idLike;
    }
    
    public void setIdLikes(Integer idLike) {
        this.idLike = idLike;
    }
    
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    
    public Post getPost() {
        return post;
    }
    
    public void setPost(Post post) {
        this.post = post;
    }
    
    
    //Metodo toString
    @Override
    public String toString() {
        return "Like: " + this.idLike + " - " + this.usuario + " - " + this.post;
    }
}