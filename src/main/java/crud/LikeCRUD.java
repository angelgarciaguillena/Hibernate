package crud;

import entities.Like;
import entities.Post;
import entities.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Scanner;

public class LikeCRUD {
    
    private Session session;
    private Scanner sc;
    private UsuarioCRUD usuarioCRUD;
    private PostCRUD postCRUD;
    
    public LikeCRUD(Session session, UsuarioCRUD usuarioCRUD, PostCRUD postCRUD) {
        this.session = session;
        this.sc = new Scanner(System.in);
        this.usuarioCRUD = usuarioCRUD;
        this.postCRUD = postCRUD;
    }
    
    public void insertar() {
        try {
            System.out.println("\nINSERTAR LIKE");
            
            long numUsuarios = usuarioCRUD.contarUsuarios();
            if (numUsuarios == 0) {
                System.out.println("No hay usuarios en la base de datos");
                System.out.println("No puedes crear un like sin un usuario valido");
            } else {
                long numPosts = postCRUD.contarPosts();
                if (numPosts == 0) {
                    System.out.println("No hay posts en la base de datos");
                    System.out.println("No puedes crear un like sin un post valido");
                } else {
                    System.out.print("Introduce el username del usuario: ");
                    String username = sc.nextLine();
                    
                    Usuario usuario = usuarioCRUD.buscarPorUsername(username);
                    
                    if (usuario == null) {
                        System.out.println("No existe ningun usuario con ese username");
                        System.out.println("No puedes crear un like sin un usuario valido en la base de datos");
                    } else {
                        System.out.print("Introduce el ID del post: ");
                        int idPost = sc.nextInt();
                        sc.nextLine();
                        
                        Post post = postCRUD.buscarPorId(idPost);
                        
                        if (post == null) {
                            System.out.println("No existe ningun post con ese ID");
                            System.out.println("No puedes crear un like sin un post valido en la base de datos");
                        } else {
                            Long existeLike = (Long) session.createQuery(
                                "SELECT COUNT(*) FROM Like l WHERE l.usuario.idUsuarios = :idUsuario AND l.post.idPosts = :idPost")
                                .setParameter("idUsuario", usuario.getIdUsuario())
                                .setParameter("idPost", idPost)
                                .uniqueResult();
                            
                            if (existeLike > 0) {
                                System.out.println("Este usuario ya ha dado like a este post");
                            } else {
                                Like like = new Like(usuario, post);
                                
                                Transaction tx = session.beginTransaction();
                                session.save(like);
                                tx.commit();
                                
                                System.out.println("Like insertado con ID: " + like.getIdLike());
                                System.out.println("  Usuario: " + usuario.getUsername());
                                System.out.println("  Post ID: " + post.getIdPost());
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error al insertar like: " + e.getMessage());
            sc.nextLine();
        }
    }
    
    public void listar() {
        System.out.println("\nLISTAR LIKES");
        System.out.println("Quieres filtrar? (S/N): ");
        String respuesta = sc.nextLine();
        
        try {
            if (respuesta.equalsIgnoreCase("S")) {
                System.out.println("Filtrar por:");
                System.out.println("1. Usuario");
                System.out.println("2. Post");
                System.out.println("3. ID del like");
                System.out.print("Selecciona: ");
                int filtro = sc.nextInt();
                sc.nextLine();
                
                if (filtro == 1) {
                    listarPorUsuario();
                } else if (filtro == 2) {
                    listarPorPost();
                } else if (filtro == 3) {
                    listarPorId();
                } else {
                    System.out.println("Opcion no valida");
                }
            } else {
                listarTodos();
            }
        } catch (Exception e) {
            System.out.println("Error al listar likes: " + e.getMessage());
            sc.nextLine();
        }
    }
    
    private void listarTodos() {
        Query<Like> query = session.createQuery("FROM Like", Like.class);
        List<Like> likes = query.list();
        mostrarLikes(likes);
    }
    
    private void listarPorUsuario() {
        System.out.print("Introduce el username: ");
        String username = sc.nextLine();
        
        Query<Like> query = session.createQuery(
            "FROM Like l WHERE l.usuario.username = :username", Like.class);
        query.setParameter("username", username);
        List<Like> likes = query.list();
        mostrarLikes(likes);
    }
    
    private void listarPorPost() {
        System.out.print("Introduce el ID del post: ");
        int idPost = sc.nextInt();
        sc.nextLine();
        
        Query<Like> query = session.createQuery(
            "FROM Like l WHERE l.post.idPosts = :idPost", Like.class);
        query.setParameter("idPost", idPost);
        List<Like> likes = query.list();
        mostrarLikes(likes);
    }
    
    private void listarPorId() {
        System.out.print("Introduce el ID del like: ");
        int id = sc.nextInt();
        sc.nextLine();
        
        Like like = session.get(Like.class, id);
        
        if (like == null) {
            System.out.println("No existe ningun like con ese ID");
        } else {
            System.out.println("\nLIKE ENCONTRADO");
            System.out.println(like);
        }
    }
    
    private void mostrarLikes(List<Like> likes) {
        if (likes.isEmpty()) {
            System.out.println("No se encontraron likes");
        } else {
            System.out.println("\nLIKES ENCONTRADOS (" + likes.size() + ")");
            for (Like l : likes) {
                System.out.println(l);
            }
        }
    }
    
    public void modificar() {
        System.out.println("\nMODIFICAR LIKE");
        System.out.println("Los likes solo se crean (dar like) o se eliminan (quitar like)");
    }
    
    public void borrar() {
        System.out.println("\nBORRAR LIKE");
        System.out.println("1. Borrar por ID");
        System.out.println("2. Borrar likes de un usuario");
        System.out.println("3. Borrar likes de un post");
        System.out.println("4. Borrar todos los likes");
        System.out.print("Selecciona opcion: ");
        
        try {
            int opcion = sc.nextInt();
            sc.nextLine();
            
            if (opcion == 1) {
                borrarPorId();
            } else if (opcion == 2) {
                borrarPorUsuario();
            } else if (opcion == 3) {
                borrarPorPost();
            } else if (opcion == 4) {
                borrarTodos();
            } else {
                System.out.println("Opcion no valida");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            sc.nextLine();
        }
    }
    
    private void borrarPorId() {
        Transaction tx = session.beginTransaction();
        try {
            System.out.print("Introduce el ID del like a borrar: ");
            int id = sc.nextInt();
            sc.nextLine();
            
            Like like = session.get(Like.class, id);
            
            if (like == null) {
                System.out.println("No existe ningun like con ese ID");
                tx.rollback();
            } else {
                System.out.println("\nLike a borrar:");
                System.out.println(like);
                
                System.out.print("\nConfirmar borrado? (S/N): ");
                String confirmar = sc.nextLine();
                
                if (confirmar.equalsIgnoreCase("S")) {
                    session.delete(like);
                    tx.commit();
                    System.out.println("Like borrado correctamente");
                } else {
                    tx.rollback();
                    System.out.println("Operacion cancelada");
                }
            }
            
        } catch (Exception e) {
            tx.rollback();
            System.out.println("Error al borrar like: " + e.getMessage());
        }
    }
    
    private void borrarPorUsuario() {
        Transaction tx = session.beginTransaction();
        try {
            System.out.print("Username del usuario: ");
            String username = sc.nextLine();
            
            Query<Like> query = session.createQuery(
                "FROM Like l WHERE l.usuario.username = :username", Like.class);
            query.setParameter("username", username);
            List<Like> likes = query.list();
            
            if (likes.isEmpty()) {
                System.out.println("No se encontraron likes de ese usuario");
                tx.rollback();
            } else {
                System.out.println("\nLikes a borrar:");
                for (Like l : likes) {
                    System.out.println(l);
                }
                
                System.out.print("\nConfirmar borrado de " + likes.size() + " like(s)? (S/N): ");
                String confirmar = sc.nextLine();
                
                if (confirmar.equalsIgnoreCase("S")) {
                    for (Like l : likes) {
                        session.delete(l);
                    }
                    tx.commit();
                    System.out.println(likes.size() + " like(s) borrado(s) correctamente");
                } else {
                    tx.rollback();
                    System.out.println("Operacion cancelada");
                }
            }
            
        } catch (Exception e) {
            tx.rollback();
            System.out.println("Error al borrar likes: " + e.getMessage());
        }
    }
    
    private void borrarPorPost() {
        Transaction tx = session.beginTransaction();
        try {
            System.out.print("ID del post: ");
            int idPost = sc.nextInt();
            sc.nextLine();
            
            Query<Like> query = session.createQuery(
                "FROM Like l WHERE l.post.idPosts = :idPost", Like.class);
            query.setParameter("idPost", idPost);
            List<Like> likes = query.list();
            
            if (likes.isEmpty()) {
                System.out.println("No se encontraron likes en ese post");
                tx.rollback();
            } else {
                System.out.println("\nLikes a borrar:");
                for (Like l : likes) {
                    System.out.println(l);
                }
                
                System.out.print("\nConfirmar borrado de " + likes.size() + " like(s)? (S/N): ");
                String confirmar = sc.nextLine();
                
                if (confirmar.equalsIgnoreCase("S")) {
                    for (Like l : likes) {
                        session.delete(l);
                    }
                    tx.commit();
                    System.out.println(likes.size() + " like(s) borrado(s) correctamente");
                } else {
                    tx.rollback();
                    System.out.println("Operacion cancelada");
                }
            }
            
        } catch (Exception e) {
            tx.rollback();
            System.out.println("Error al borrar likes: " + e.getMessage());
        }
    }
    
    private void borrarTodos() {
        Transaction tx = session.beginTransaction();
        try {
            int deleted = session.createQuery("DELETE FROM Like").executeUpdate();
            
            System.out.println(deleted + " like(s) seran eliminados.");
            System.out.print("Confirmar borrado? (S/N): ");
            String confirmar = sc.nextLine();
            
            if (confirmar.equalsIgnoreCase("S")) {
                tx.commit();
                System.out.println("Todos los likes borrados correctamente");
            } else {
                tx.rollback();
                System.out.println("Operacion cancelada");
            }
            
        } catch (Exception e) {
            tx.rollback();
            System.out.println("Error al borrar likes: " + e.getMessage());
        }
    }
    
    public long contarLikes() {
        long resultado = 0;
        try {
            Long count = (Long) session.createQuery("SELECT COUNT(*) FROM Like").uniqueResult();
            resultado = count != null ? count : 0;
        } catch (Exception e) {
            System.out.println("Error al contar likes: " + e.getMessage());
            resultado = 0;
        }
        return resultado;
    }
    
    public long contarLikesDePost(int idPost) {
        long resultado = 0;
        try {
            Long count = (Long) session.createQuery(
                "SELECT COUNT(*) FROM Like l WHERE l.post.idPosts = :idPost")
                .setParameter("idPost", idPost)
                .uniqueResult();
            resultado = count != null ? count : 0;
        } catch (Exception e) {
            System.out.println("Error al contar likes del post: " + e.getMessage());
            resultado = 0;
        }
        return resultado;
    }
    
    public long contarLikesDeUsuario(String username) {
        long resultado = 0;
        try {
            Long count = (Long) session.createQuery(
                "SELECT COUNT(*) FROM Like l WHERE l.usuario.username = :username")
                .setParameter("username", username)
                .uniqueResult();
            resultado = count != null ? count : 0;
        } catch (Exception e) {
            System.out.println("Error al contar likes del usuario: " + e.getMessage());
            resultado = 0;
        }
        return resultado;
    }
}