package main;

import crud.LikeCRUD;
import crud.PostCRUD;
import crud.UsuarioCRUD;
import org.hibernate.Session;
import org.hibernate.Transaction;
import utils.HibernateUtil;

import java.util.Scanner;

public class Main {
    
    private static Session session;
    private static UsuarioCRUD usuarioCRUD;
    private static PostCRUD postCRUD;
    private static LikeCRUD likeCRUD;
    private static Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) {
        
        conectar();
        
        boolean salir = false;
        
        while (!salir) {
            mostrarMenu();
            
            try {
                int opcion = sc.nextInt();
                sc.nextLine();
                
                if (opcion == 1) {
                    conectar();
                } else if (opcion == 2) {
                    crearTablas();
                } else if (opcion == 3) {
                    insertar();
                } else if (opcion == 4) {
                    listar();
                } else if (opcion == 5) {
                    modificar();
                } else if (opcion == 6) {
                    borrar();
                } else if (opcion == 7) {
                    eliminarTablas();
                } else if (opcion == 0) {
                    System.out.println("\nEstas seguro de que quieres salir? (S/N): ");
                    String confirmar = sc.nextLine();
                    if (confirmar.equalsIgnoreCase("S")) {
                        salir = true;
                        cerrarConexion();
                        System.out.println("\nHas salido del programa");
                    }
                } else {
                    System.out.println("Opcion no valida. Por favor selecciona una opcion del menu.");
                }
                
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Por favor, introduce un numero valido.");
                sc.nextLine();
            }
            
            if (!salir) {
                System.out.println("\nPresiona ENTER para continuar...");
                sc.nextLine();
            }
        }
        
        sc.close();
    }
    
    private static void conectar() {
        try {
            if (session == null || !session.isOpen()) {
                session = HibernateUtil.getSessionFactory().openSession();
                
                usuarioCRUD = new UsuarioCRUD(session);
                postCRUD = new PostCRUD(session, usuarioCRUD);
                likeCRUD = new LikeCRUD(session, usuarioCRUD, postCRUD);
                
                System.out.println("Conexion establecida con exito");
            }
        } catch (Exception e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
    }
    
    private static void crearTablas() {
        System.out.println("\nCREAR TABLAS");
        System.out.println("1. Crear todas las tablas");
        System.out.println("2. Crear tabla especifica");
        System.out.print("Selecciona opcion: ");
        
        int opcion = sc.nextInt();
        sc.nextLine();
        
        if (opcion == 1) {
            System.out.println("Las tablas se han creado");
            System.out.println("Tablas: Usuarios, Posts, Likes creadas");
        } else if (opcion == 2) {
            System.out.println("Que tabla quieres crear?");
            System.out.println("1. Usuarios");
            System.out.println("2. Posts");
            System.out.println("3. Likes");
            int tabla = sc.nextInt();
            sc.nextLine();
            
            if (tabla == 2 || tabla == 3) {
                System.out.println("Esta tabla tiene dependencias. Asegurate de crear primero:");
                if (tabla == 2) {
                    System.out.println("Usuarios");
                }
                if (tabla == 3) {
                    System.out.println("Usuarios y Posts");
                }
            }
            System.out.println("Tabla creada");
        }
    }
    
    private static void insertar() {
        System.out.println("\nINSERTAR DATOS");
        System.out.println("En que tabla quieres insertar?");
        System.out.println("1. Usuarios");
        System.out.println("2. Posts");
        System.out.println("3. Likes");
        System.out.print("Selecciona opcion: ");
        
        int opcion = sc.nextInt();
        sc.nextLine();
        
        if (opcion == 1) {
            usuarioCRUD.insertar();
        } else if (opcion == 2) {
            postCRUD.insertar();
        } else if (opcion == 3) {
            likeCRUD.insertar();
        } else {
            System.out.println("Opcion no valida");
        }
    }
    
    private static void listar() {
        System.out.println("\nLISTAR DATOS");
        System.out.println("Que tabla quieres listar?");
        System.out.println("1. Usuarios");
        System.out.println("2. Posts");
        System.out.println("3. Likes");
        System.out.println("4. Todas las tablas");
        System.out.print("Selecciona opcion: ");
        
        int opcion = sc.nextInt();
        sc.nextLine();
        
        if (opcion == 1) {
            usuarioCRUD.listar();
        } else if (opcion == 2) {
            postCRUD.listar();
        } else if (opcion == 3) {
            likeCRUD.listar();
        } else if (opcion == 4) {
            usuarioCRUD.listar();
            postCRUD.listar();
            likeCRUD.listar();
        } else {
            System.out.println("Opcion no valida");
        }
    }
    
    private static void modificar() {
        System.out.println("\nMODIFICAR DATOS");
        System.out.println("Que tabla quieres modificar?");
        System.out.println("1. Usuarios");
        System.out.println("2. Posts");
        System.out.println("3. Likes");
        System.out.print("Selecciona opcion: ");
        
        int opcion = sc.nextInt();
        sc.nextLine();
        
        if (opcion == 1) {
            usuarioCRUD.modificar();
        } else if (opcion == 2) {
            postCRUD.modificar();
        } else if (opcion == 3) {
            likeCRUD.modificar();
        } else {
            System.out.println("Opcion no valida");
        }
    }
    
    private static void borrar() {
        System.out.println("\nBORRAR DATOS");
        System.out.println("De que tabla quieres borrar?");
        System.out.println("1. Usuarios");
        System.out.println("2. Posts");
        System.out.println("3. Likes");
        System.out.println("4. Todos los datos de todas las tablas");
        System.out.print("Selecciona opcion: ");
        
        int opcion = sc.nextInt();
        sc.nextLine();
        
        if (opcion == 1) {
            usuarioCRUD.borrar();
        } else if (opcion == 2) {
            postCRUD.borrar();
        } else if (opcion == 3) {
            likeCRUD.borrar();
        } else if (opcion == 4) {
            borrarTodosDatos();
        } else {
            System.out.println("Opcion no valida");
        }
    }
    
    private static void borrarTodosDatos() {
        Transaction tx = session.beginTransaction();
        try {
            System.out.println("ADVERTENCIA: Esto borrara TODOS los datos de TODAS las tablas");
            System.out.print("Estas seguro? (S/N): ");
            String confirmar = sc.nextLine();
            
            if (confirmar.equalsIgnoreCase("S")) {
                int likesDeleted = session.createQuery("DELETE FROM Like").executeUpdate();
                int postsDeleted = session.createQuery("DELETE FROM Post").executeUpdate();
                int usuariosDeleted = session.createQuery("DELETE FROM Usuario").executeUpdate();
                
                tx.commit();
                System.out.println("Todos los datos borrados:");
                System.out.println("  - " + likesDeleted + " likes");
                System.out.println("  - " + postsDeleted + " posts");
                System.out.println("  - " + usuariosDeleted + " usuarios");
            } else {
                tx.rollback();
                System.out.println("Operacion cancelada");
            }
        } catch (Exception e) {
            tx.rollback();
            System.out.println("Error al borrar: " + e.getMessage());
        }
    }
    
    private static void eliminarTablas() {
        System.out.println("\nELIMINAR TABLAS");
        System.out.println("ADVERTENCIA: Esto eliminara las tablas completamente");
        System.out.println("1. Eliminar todas las tablas");
        System.out.println("2. Eliminar una tabla especifica");
        System.out.print("Selecciona opcion: ");
        
        int opcion = sc.nextInt();
        sc.nextLine();
        
        Transaction tx = session.beginTransaction();
        try {
            if (opcion == 1) {
                System.out.print("Estas seguro? Esto borrara TODAS las tablas (S/N): ");
                String confirmar = sc.nextLine();
                
                if (confirmar.equalsIgnoreCase("S")) {
                    session.createNativeQuery("DROP TABLE IF EXISTS Likes").executeUpdate();
                    session.createNativeQuery("DROP TABLE IF EXISTS Posts").executeUpdate();
                    session.createNativeQuery("DROP TABLE IF EXISTS Usuarios").executeUpdate();
                    tx.commit();
                    System.out.println("Todas las tablas eliminadas");
                } else {
                    tx.rollback();
                    System.out.println("Operacion cancelada");
                }
            } else if (opcion == 2) {
                System.out.println("Que tabla quieres eliminar?");
                System.out.println("1. Usuarios");
                System.out.println("2. Posts");
                System.out.println("3. Likes");
                int tabla = sc.nextInt();
                sc.nextLine();
                
                String nombreTabla = "";
                boolean tieneDependencias = false;
                
                if (tabla == 1) {
                    nombreTabla = "Usuarios";
                    System.out.println("Debes eliminar primero: Posts y Likes");
                    tieneDependencias = true;
                } else if (tabla == 2) {
                    nombreTabla = "Posts";
                    System.out.println("Debes eliminar primero: Likes");
                    tieneDependencias = true;
                } else if (tabla == 3) {
                    nombreTabla = "Likes";
                }
                
                if (!tieneDependencias || tabla == 3) {
                    System.out.print("Confirmar eliminacion de " + nombreTabla + "? (S/N): ");
                    String confirmar = sc.nextLine();
                    
                    if (confirmar.equalsIgnoreCase("S")) {
                        session.createNativeQuery("DROP TABLE IF EXISTS " + nombreTabla).executeUpdate();
                        tx.commit();
                        System.out.println("Tabla " + nombreTabla + " eliminada");
                    } else {
                        tx.rollback();
                        System.out.println("Operacion cancelada");
                    }
                } else {
                    tx.rollback();
                }
            }
        } catch (Exception e) {
            tx.rollback();
            System.out.println("Error al eliminar tablas: " + e.getMessage());
        }
    }
    
    private static void cerrarConexion() {
        if (session != null && session.isOpen()) {
            session.close();
            System.out.println("Conexion cerrada");
        }
        HibernateUtil.shutdown();
    }
    
    private static void mostrarMenu() {
        System.out.println("\nMENU PRINCIPAL");
        System.out.println("1. Conectar a la base de datos");
        System.out.println("2. Crear tablas");
        System.out.println("3. Insertar datos");
        System.out.println("4. Listar datos");
        System.out.println("5. Modificar datos");
        System.out.println("6. Borrar datos");
        System.out.println("7. Eliminar tablas");
        System.out.println("0. Salir");
        System.out.print("Selecciona una opcion: ");
    }
}