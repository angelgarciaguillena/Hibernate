package entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "Posts")
public class Post {
	
	//Atributos
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPosts")
    private Integer idPost;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idUsuarios", nullable = false)
    private Usuario usuario;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.DATE)
    private Date createdAt;
    
    @Column(name = "updated_at")
    @Temporal(TemporalType.DATE)
    private Date updatedAt;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();
	
	
	//Constructores
	public Post() {
		
	}
	
	public Post(Usuario usuario, Date createdAt, Date updatedAt) {
		
		this.usuario = usuario;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	
	//Getters y Setters
	public Integer getIdPost() {
		return idPost;
	}

	public void setIdPost(Integer idPost) {
		this.idPost = idPost;
	}

	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	
	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	
	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	
	public List<Like> getLikes() {
		return likes;
	}

	public void setLikes(List<Like> likes) {
		this.likes = likes;
	}
	
	
	//Metodo toString
	@Override
	public String toString() {
		return "Post: " + this.idPost + " - " + this.usuario + " - " + this.createdAt + " - " + this.updatedAt;
	}
}