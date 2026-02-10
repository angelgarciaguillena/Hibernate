package crud;

import entities.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Scanner;

public class UsuarioCRUD {
    
    private Session session;
    private Scanner sc;
    
    public UsuarioCRUD(Session session) {
        this.session = session;
        this.sc = new Scanner(System.in);
    }
    
    public void insertar() {
        try {
            System.out.println("\nINSERTAR USUARIO");
            
            System.out.print("Nombre: ");
            String nombre = sc.nextLine();
            
            System.out.print("Apellidos: ");
            String apellidos = sc.nextLine();
            
            System.out.print("Username (max 12 caracteres): ");
            String username = sc.nextLine();
            
            System.out.print("Password: ");
            String password = sc.nextLine();
            
            System.out.print("Email: ");
            String email = sc.nextLine();
            
            Usuario usuario = new Usuario(nombre, apellidos, username, password, email);
            
            Transaction tx = session.beginTransaction();
            session.save(usuario);
            tx.commit();
            
            System.out.println("Usuario insertado con ID: " + usuario.getIdUsuario());
            
        } catch (Exception e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            if (e.getMessage().contains("Duplicate")) {
                System.out.println("El username ya existe en la base de datos");
            }
        }
    }
    
    public void listar() {
        System.out.println("\nLISTAR USUARIOS");
        System.out.println("Quieres filtrar? (S/N): ");
        String respuesta = sc.nextLine();
        
        try {
            if (respuesta.equalsIgnoreCase("S")) {
                System.out.println("Filtrar por:");
                System.out.println("1. Nombre (=)");
                System.out.println("2. Apellidos (LIKE)");
                System.out.println("3. Username (=)");
                System.out.print("Selecciona: ");
                int filtro = sc.nextInt();
                sc.nextLine();
                
                if (filtro == 1) {
                    listarPorNombre();
                } else if (filtro == 2) {
                    listarPorApellidos();
                } else if (filtro == 3) {
                    listarPorUsername();
                } else {
                    System.out.println("Opcion no valida");
                }
            } else {
                listarTodos();
            }
        } catch (Exception e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
            sc.nextLine();
        }
    }
    
    private void listarTodos() {
        Query<Usuario> query = session.createQuery("FROM Usuario", Usuario.class);
        List<Usuario> usuarios = query.list();
        mostrarUsuarios(usuarios);
    }
    
    private void listarPorNombre() {
        System.out.print("Introduce el nombre: ");
        String nombre = sc.nextLine();
        Query<Usuario> query = session.createQuery(
            "FROM Usuario WHERE nombre = :nombre", Usuario.class);
        query.setParameter("nombre", nombre);
        List<Usuario> usuarios = query.list();
        mostrarUsuarios(usuarios);
    }
    
    private void listarPorApellidos() {
        System.out.print("Introduce los apellidos: ");
        String apellidos = sc.nextLine();
        Query<Usuario> query = session.createQuery(
            "FROM Usuario WHERE apellidos LIKE :apellidos", Usuario.class);
        query.setParameter("apellidos", "%" + apellidos + "%");
        List<Usuario> usuarios = query.list();
        mostrarUsuarios(usuarios);
    }
    
    private void listarPorUsername() {
        System.out.print("Introduce el username: ");
        String username = sc.nextLine();
        Query<Usuario> query = session.createQuery(
            "FROM Usuario WHERE username = :username", Usuario.class);
        query.setParameter("username", username);
        List<Usuario> usuarios = query.list();
        mostrarUsuarios(usuarios);
    }
    
    private void mostrarUsuarios(List<Usuario> usuarios) {
        if (usuarios.isEmpty()) {
            System.out.println("No se encontraron usuarios");
        } else {
            System.out.println("\nUSUARIOS ENCONTRADOS");
            for (Usuario u : usuarios) {
                System.out.println(u);
            }
        }
    }
    
    public void modificar() {
        try {
            System.out.println("\nMODIFICAR USUARIO");
            System.out.print("Introduce el ID del usuario a modificar: ");
            int id = sc.nextInt();
            sc.nextLine();
            
            Usuario usuario = session.get(Usuario.class, id);
            
            if (usuario == null) {
                System.out.println("No existe ningun usuario con ese ID");
            } else {
                System.out.println("Usuario actual: " + usuario);
                
                Transaction tx = session.beginTransaction();
                
                System.out.print("Nuevo nombre (actual: " + usuario.getNombre() + ") [Enter para no cambiar]: ");
                String nombre = sc.nextLine();
                if (!nombre.isEmpty()) {
                    usuario.setNombre(nombre);
                }
                
                System.out.print("Nuevos apellidos (actual: " + usuario.getApellidos() + ") [Enter para no cambiar]: ");
                String apellidos = sc.nextLine();
                if (!apellidos.isEmpty()) {
                    usuario.setApellidos(apellidos);
                }
                
                System.out.print("Nuevo username (actual: " + usuario.getUsername() + ") [Enter para no cambiar]: ");
                String username = sc.nextLine();
                if (!username.isEmpty()) {
                    usuario.setUsername(username);
                }
                
                System.out.print("Nueva password [Enter para no cambiar]: ");
                String password = sc.nextLine();
                if (!password.isEmpty()) {
                    usuario.setPassword(password);
                }
                
                System.out.print("Nuevo email (actual: " + usuario.getEmail() + ") [Enter para no cambiar]: ");
                String email = sc.nextLine();
                if (!email.isEmpty()) {
                    usuario.setEmail(email);
                }
                
                session.update(usuario);
                
                System.out.println("\nUSUARIO MODIFICADO");
                System.out.println(usuario);
                
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
            
        } catch (Exception e) {
            System.out.println("Error al modificar usuario: " + e.getMessage());
            sc.nextLine();
        }
    }
    
    public void borrar() {
        System.out.println("\nBORRAR USUARIO");
        System.out.println("1. Borrar por ID");
        System.out.println("2. Borrar por nombre");
        System.out.println("3. Borrar todos los usuarios");
        System.out.print("Selecciona opcion: ");
        
        try {
            int opcion = sc.nextInt();
            sc.nextLine();
            
            if (opcion == 1) {
                borrarPorId();
            } else if (opcion == 2) {
                borrarPorNombre();
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
            System.out.print("Introduce el ID del usuario a borrar: ");
            int id = sc.nextInt();
            sc.nextLine();
            
            Usuario usuario = session.get(Usuario.class, id);
            
            if (usuario == null) {
                System.out.println("No existe ningun usuario con ese ID");
                tx.rollback();
            } else {
                Long numPosts = (Long) session.createQuery(
                    "SELECT COUNT(*) FROM Post p WHERE p.usuario.idUsuarios = :id")
                    .setParameter("id", id)
                    .uniqueResult();
                
                Long numLikes = (Long) session.createQuery(
                    "SELECT COUNT(*) FROM Like l WHERE l.usuario.idUsuarios = :id")
                    .setParameter("id", id)
                    .uniqueResult();
                
                if (numPosts > 0 || numLikes > 0) {
                    System.out.println("No se puede borrar este usuario porque tiene:");
                    if (numPosts > 0) {
                        System.out.println("  - " + numPosts + " post(s)");
                    }
                    if (numLikes > 0) {
                        System.out.println("  - " + numLikes + " like(s)");
                    }
                    System.out.println("Debes borrar primero sus posts y likes");
                    tx.rollback();
                } else {
                    System.out.println("\nUsuario a borrar:");
                    System.out.println(usuario);
                    
                    System.out.print("\nConfirmar borrado? (S/N): ");
                    String confirmar = sc.nextLine();
                    
                    if (confirmar.equalsIgnoreCase("S")) {
                        session.delete(usuario);
                        tx.commit();
                        System.out.println("Usuario borrado correctamente");
                    } else {
                        tx.rollback();
                        System.out.println("Operacion cancelada");
                    }
                }
            }
            
        } catch (Exception e) {
            tx.rollback();
            System.out.println("Error al borrar usuario: " + e.getMessage());
        }
    }
    
    private void borrarPorNombre() {
        Transaction tx = session.beginTransaction();
        try {
            System.out.print("Nombre del usuario a borrar: ");
            String nombre = sc.nextLine();
            
            Query<Usuario> query = session.createQuery(
                "FROM Usuario WHERE nombre = :nombre", Usuario.class);
            query.setParameter("nombre", nombre);
            List<Usuario> usuarios = query.list();
            
            if (usuarios.isEmpty()) {
                System.out.println("No se encontraron usuarios con ese nombre");
                tx.rollback();
            } else {
                System.out.println("\nUsuarios a borrar:");
                for (Usuario u : usuarios) {
                    System.out.println(u);
                }
                
                System.out.print("\nConfirmar borrado de " + usuarios.size() + " usuario(s)? (S/N): ");
                String confirmar = sc.nextLine();
                
                if (confirmar.equalsIgnoreCase("S")) {
                    for (Usuario u : usuarios) {
                        session.delete(u);
                    }
                    tx.commit();
                    System.out.println(usuarios.size() + " usuario(s) borrado(s) correctamente");
                } else {
                    tx.rollback();
                    System.out.println("Operacion cancelada");
                }
            }
            
        } catch (Exception e) {
            tx.rollback();
            System.out.println("Error al borrar usuarios: " + e.getMessage());
            if (e.getMessage().contains("foreign key constraint")) {
                System.out.println("Algunos usuarios tienen posts o likes asociados");
            }
        }
    }
    
    private void borrarTodos() {
        Transaction tx = session.beginTransaction();
        try {
            Long numUsuariosConPosts = (Long) session.createQuery(
                "SELECT COUNT(DISTINCT p.usuario.idUsuarios) FROM Post p")
                .uniqueResult();
            
            Long numUsuariosConLikes = (Long) session.createQuery(
                "SELECT COUNT(DISTINCT l.usuario.idUsuarios) FROM Like l")
                .uniqueResult();
            
            if (numUsuariosConPosts > 0 || numUsuariosConLikes > 0) {
                System.out.println("No se pueden borrar todos los usuarios porque algunos tienen:");
                if (numUsuariosConPosts > 0) {
                    System.out.println("  - Posts asociados");
                }
                if (numUsuariosConLikes > 0) {
                    System.out.println("  - Likes asociados");
                }
                System.out.println("Debes borrar primero los posts y likes");
                tx.rollback();
            } else {
                int deleted = session.createQuery("DELETE FROM Usuario").executeUpdate();
                
                System.out.println(deleted + " usuario(s) seran eliminados.");
                System.out.print("Confirmar borrado? (S/N): ");
                String confirmar = sc.nextLine();
                
                if (confirmar.equalsIgnoreCase("S")) {
                    tx.commit();
                    System.out.println("Todos los usuarios borrados correctamente");
                } else {
                    tx.rollback();
                    System.out.println("Operacion cancelada");
                }
            }
            
        } catch (Exception e) {
            tx.rollback();
            System.out.println("Error al borrar usuarios: " + e.getMessage());
        }
    }
    
    public Usuario buscarPorUsername(String username) {
        Usuario resultado = null;
        try {
            Query<Usuario> query = session.createQuery(
                "FROM Usuario WHERE username = :username", Usuario.class);
            query.setParameter("username", username);
            List<Usuario> usuarios = query.list();
            
            if (usuarios.isEmpty()) {
                resultado = null;
            } else if (usuarios.size() == 1) {
                resultado = usuarios.get(0);
            } else {
                System.out.println("Se encontraron varios usuarios:");
                for (int i = 0; i < usuarios.size(); i++) {
                    System.out.println((i + 1) + ". " + usuarios.get(i));
                }
                System.out.print("Selecciona el numero de usuario: ");
                int seleccion = sc.nextInt() - 1;
                sc.nextLine();
                
                if (seleccion >= 0 && seleccion < usuarios.size()) {
                    resultado = usuarios.get(seleccion);
                } else {
                    System.out.println("Seleccion no valida");
                    resultado = null;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
            resultado = null;
        }
        
        return resultado;
    }
    
    public long contarUsuarios() {
        long resultado = 0;
        try {
            Long count = (Long) session.createQuery("SELECT COUNT(*) FROM Usuario").uniqueResult();
            resultado = count != null ? count : 0;
        } catch (Exception e) {
            System.out.println("Error al contar usuarios: " + e.getMessage());
            resultado = 0;
        }
        return resultado;
    }
}