package entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "Usuarios")
public class Usuario {
	
	//Atributos
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuarios")
	private Integer idUsuario;
	
	@Column(name = "Nombre", length = 45)
	private String nombre;
	
	@Column(name = "Apellidos", length = 45)
	private String apellidos;
	
	@Column(name = "Username", length = 12, unique = true)
	private String username;
	
	@Column(name = "Password", length = 128)
	private String password;
	
	@Column(name = "email", length = 50)
	private String email;
	
	@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();
	
	
    //Constructores
	public Usuario() {
		
	}
	
	public Usuario(String nombre, String apellidos, String username, String password, String email) {
		
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.username = username;
		this.password = password;
		this.email = email;
	}

	
	//Getters y Setters
	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	
	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
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
		return "Usuario: " + this.idUsuario + " - " + this.nombre + " - " + this.apellidos + " - " + this.username + " - " + this.email;
	}
}