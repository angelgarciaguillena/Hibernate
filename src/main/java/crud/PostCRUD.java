package crud;

import entities.Post;
import entities.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class PostCRUD {
    
    private Session session;
    private Scanner sc;
    private UsuarioCRUD usuarioCRUD;
    
    public PostCRUD(Session session, UsuarioCRUD usuarioCRUD) {
        this.session = session;
        this.sc = new Scanner(System.in);
        this.usuarioCRUD = usuarioCRUD;
    }
    
    public void insertar() {
        try {
            System.out.println("\nINSERTAR POST");
            
            long numUsuarios = usuarioCRUD.contarUsuarios();
            if (numUsuarios == 0) {
                System.out.println("No hay usuarios en la base de datos");
                System.out.println("No puedes crear un post sin un usuario valido");
            } else {
                System.out.print("Introduce el username del usuario: ");
                String username = sc.nextLine();
                
                Usuario usuario = usuarioCRUD.buscarPorUsername(username);
                
                if (usuario == null) {
                    System.out.println("No existe ningun usuario con ese username");
                    System.out.println("No puedes crear un post sin un usuario valido en la base de datos");
                } else {
                    Date ahora = new Date();
                    Post post = new Post(usuario, ahora, ahora);
                    
                    Transaction tx = session.beginTransaction();
                    session.save(post);
                    tx.commit();
                    
                    System.out.println("Post insertado con ID: " + post.getIdPost());
                    System.out.println("  Usuario: " + usuario.getUsername());
                    System.out.println("  Fecha creacion: " + ahora);
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error al insertar post: " + e.getMessage());
        }
    }
    
    public void listar() {
        System.out.println("\nLISTAR POSTS");
        System.out.println("Quieres filtrar? (S/N): ");
        String respuesta = sc.nextLine();
        
        try {
            if (respuesta.equalsIgnoreCase("S")) {
                System.out.println("Filtrar por:");
                System.out.println("1. Usuario (username)");
                System.out.println("2. Fecha de creacion");
                System.out.println("3. ID del post");
                System.out.print("Selecciona: ");
                int filtro = sc.nextInt();
                sc.nextLine();
                
                if (filtro == 1) {
                    listarPorUsuario();
                } else if (filtro == 2) {
                    listarPorFecha();
                } else if (filtro == 3) {
                    listarPorId();
                } else {
                    System.out.println("Opcion no valida");
                }
            } else {
                listarTodos();
            }
        } catch (Exception e) {
            System.out.println("Error al listar posts: " + e.getMessage());
            sc.nextLine();
        }
    }
    
    private void listarTodos() {
        Query<Post> query = session.createQuery("FROM Post ORDER BY createdAt DESC", Post.class);
        List<Post> posts = query.list();
        mostrarPosts(posts);
    }
    
    private void listarPorUsuario() {
        System.out.print("Introduce el username: ");
        String username = sc.nextLine();
        
        Query<Post> query = session.createQuery(
            "FROM Post p WHERE p.usuario.username = :username ORDER BY p.createdAt DESC", Post.class);
        query.setParameter("username", username);
        List<Post> posts = query.list();
        mostrarPosts(posts);
    }
    
    private void listarPorFecha() {
        System.out.print("Introduce la fecha (yyyy-MM-dd): ");
        String fechaStr = sc.nextLine();
        
        System.out.println("Quieres posts creados antes (<) o despues (>) de esta fecha?");
        System.out.print("Introduce < o >: ");
        String operador = sc.nextLine();
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fecha = sdf.parse(fechaStr);
            
            String hql = operador.equals("<") 
                ? "FROM Post WHERE createdAt < :fecha ORDER BY createdAt DESC"
                : "FROM Post WHERE createdAt > :fecha ORDER BY createdAt DESC";
            
            Query<Post> query = session.createQuery(hql, Post.class);
            query.setParameter("fecha", fecha);
            List<Post> posts = query.list();
            mostrarPosts(posts);
            
        } catch (ParseException e) {
            System.out.println("Formato de fecha incorrecto. Usa yyyy-MM-dd");
        }
    }
    
    private void listarPorId() {
        System.out.print("Introduce el ID del post: ");
        int id = sc.nextInt();
        sc.nextLine();
        
        Post post = session.get(Post.class, id);
        
        if (post == null) {
            System.out.println("No existe ningun post con ese ID");
        } else {
            System.out.println("\nPOST ENCONTRADO");
            System.out.println(post);
            System.out.println("Numero de likes: " + post.getLikes().size());
        }
    }
    
    private void mostrarPosts(List<Post> posts) {
        if (posts.isEmpty()) {
            System.out.println("No se encontraron posts");
        } else {
            System.out.println("\nPOSTS ENCONTRADOS (" + posts.size() + ")");
            for (Post p : posts) {
                System.out.println(p);
            }
        }
    }
    
    public void modificar() {
        try {
            System.out.println("\nMODIFICAR POST");
            System.out.print("Introduce el ID del post a modificar: ");
            int id = sc.nextInt();
            sc.nextLine();
            
            Post post = session.get(Post.class, id);
            
            if (post == null) {
                System.out.println("No existe ningun post con ese ID");
            } else {
                System.out.println("Post actual: " + post);
                
                Transaction tx = session.beginTransaction();
                
                System.out.println("\nQue deseas modificar?");
                System.out.println("1. Actualizar fecha de modificacion (updated_at)");
                System.out.println("2. Cambiar usuario propietario");
                System.out.print("Selecciona opcion: ");
                int opcion = sc.nextInt();
                sc.nextLine();
                
                if (opcion == 1) {
                    post.setUpdatedAt(new Date());
                    System.out.println("Fecha de modificacion actualizada");
                    
                    session.update(post);
                    
                    System.out.println("\n--- POST MODIFICADO ---");
                    System.out.println(post);
                    
                    System.out.print("\nConfirmar cambios? (S/N): ");
                    String confirmar = sc.nextLine();
                    
                    if (confirmar.equalsIgnoreCase("S")) {
                        tx.commit();
                        System.out.println("Cambios guardados correctamente");
                    } else {
                        tx.rollback();
                        System.out.println("Cambios descartados");
                    }
                } else if (opcion == 2) {
                    System.out.print("Nuevo username del usuario: ");
                    String username = sc.nextLine();
                    
                    Usuario nuevoUsuario = usuarioCRUD.buscarPorUsername(username);
                    
                    if (nuevoUsuario == null) {
                        System.out.println("No existe ese usuario");
                        tx.rollback();
                    } else {
                        post.setUsuario(nuevoUsuario);
                        post.setUpdatedAt(new Date());
                        System.out.println("Usuario propietario cambiado");
                        
                        session.update(post);
                        
                        System.out.println("\n--- POST MODIFICADO ---");
                        System.out.println(post);
                        
                        System.out.print("\nConfirmar cambios? (S/N): ");
                        String confirmar = sc.nextLine();
                        
                        if (confirmar.equalsIgnoreCase("S")) {
                            tx.commit();
                            System.out.println("Cambios guardados correctamente");
                        } else {
                            tx.rollback();
                            System.out.println("Cambios descartados");
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("Error al modificar post: " + e.getMessage());
            sc.nextLine();
        }
    }
    
    public void borrar() {
        System.out.println("\nBORRAR POST");
        System.out.println("1. Borrar por ID");
        System.out.println("2. Borrar posts de un usuario");
        System.out.println("3. Borrar todos los posts");
        System.out.print("Selecciona opcion: ");
        
        try {
            int opcion = sc.nextInt();
            sc.nextLine();
            
            if (opcion == 1) {
                borrarPorId();
            } else if (opcion == 2) {
                borrarPorUsuario();
            } else if (opcion == 3) {
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
            System.out.print("Introduce el ID del post a borrar: ");
            int id = sc.nextInt();
            sc.nextLine();
            
            Post post = session.get(Post.class, id);
            
            if (post == null) {
                System.out.println("No existe ningun post con ese ID");
                tx.rollback();
            } else {
                Long numLikes = (Long) session.createQuery(
                    "SELECT COUNT(*) FROM Like l WHERE l.post.idPosts = :id")
                    .setParameter("id", id)
                    .uniqueResult();
                
                if (numLikes > 0) {
                    System.out.println("No se puede borrar este post porque tiene " + numLikes + " like(s)");
                    System.out.println("Debes borrar primero los likes asociados");
                    tx.rollback();
                } else {
                    System.out.println("\nPost a borrar:");
                    System.out.println(post);
                    
                    System.out.print("\nConfirmar borrado? (S/N): ");
                    String confirmar = sc.nextLine();
                    
                    if (confirmar.equalsIgnoreCase("S")) {
                        session.delete(post);
                        tx.commit();
                        System.out.println("Post borrado correctamente");
                    } else {
                        tx.rollback();
                        System.out.println("Operacion cancelada");
                    }
                }
            }
            
        } catch (Exception e) {
            tx.rollback();
            System.out.println("Error al borrar post: " + e.getMessage());
        }
    }
    
    private void borrarPorUsuario() {
        Transaction tx = session.beginTransaction();
        try {
            System.out.print("Username del usuario: ");
            String username = sc.nextLine();
            
            Query<Post> query = session.createQuery(
                "FROM Post p WHERE p.usuario.username = :username", Post.class);
            query.setParameter("username", username);
            List<Post> posts = query.list();
            
            if (posts.isEmpty()) {
                System.out.println("No se encontraron posts de ese usuario");
                tx.rollback();
            } else {
                System.out.println("\nPosts a borrar:");
                for (Post p : posts) {
                    System.out.println(p);
                }
                
                System.out.print("\nConfirmar borrado de " + posts.size() + " post(s)? (S/N): ");
                String confirmar = sc.nextLine();
                
                if (confirmar.equalsIgnoreCase("S")) {
                    for (Post p : posts) {
                        session.delete(p);
                    }
                    tx.commit();
                    System.out.println(posts.size() + " post(s) borrado(s) correctamente");
                } else {
                    tx.rollback();
                    System.out.println("Operacion cancelada");
                }
            }
            
        } catch (Exception e) {
            tx.rollback();
            System.out.println("Error al borrar posts: " + e.getMessage());
            if (e.getMessage().contains("foreign key constraint")) {
                System.out.println("Algunos posts tienen likes asociados");
            }
        }
    }
    
    private void borrarTodos() {
        Transaction tx = session.beginTransaction();
        try {
            Long numPostsConLikes = (Long) session.createQuery(
                "SELECT COUNT(DISTINCT l.post.idPosts) FROM Like l")
                .uniqueResult();
            
            if (numPostsConLikes > 0) {
                System.out.println("No se pueden borrar todos los posts porque " + numPostsConLikes + " tienen likes");
                System.out.println("Debes borrar primero todos los likes");
                tx.rollback();
            } else {
                int deleted = session.createQuery("DELETE FROM Post").executeUpdate();
                
                System.out.println(deleted + " post(s) seran eliminados.");
                System.out.print("Confirmar borrado? (S/N): ");
                String confirmar = sc.nextLine();
                
                if (confirmar.equalsIgnoreCase("S")) {
                    tx.commit();
                    System.out.println("Todos los posts borrados correctamente");
                } else {
                    tx.rollback();
                    System.out.println("Operacion cancelada");
                }
            }
            
        } catch (Exception e) {
            tx.rollback();
            System.out.println("Error al borrar posts: " + e.getMessage());
        }
    }
    
    public Post buscarPorId(int id) {
        Post resultado = null;
        try {
            resultado = session.get(Post.class, id);
        } catch (Exception e) {
            System.out.println("Error al buscar post: " + e.getMessage());
            resultado = null;
        }
        return resultado;
    }
    
    public long contarPosts() {
        long resultado = 0;
        try {
            Long count = (Long) session.createQuery("SELECT COUNT(*) FROM Post").uniqueResult();
            resultado = count != null ? count : 0;
        } catch (Exception e) {
            System.out.println("Error al contar posts: " + e.getMessage());
            resultado = 0;
        }
        return resultado;
    }
}