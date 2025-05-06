import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class GestionBibliotecaMuskiz {
    public static void main(String[] args) throws Exception {
        Connection conn = connectMySQL();
        if (conn != null) {

            // Crear objeto scanner
            Scanner scanner = new Scanner(System.in);
            String opcion = ""; // Variable para la opción del menú principal

            // Encabezado
            System.out.println("=========================================================");
            System.out.println("=============== Gestión Biblioteca Muskiz ===============");
            System.out.println("=========================================================");
            System.out.println("Bienvenido al programa Gestor de la Biblioteca de Muskiz");

            // Bucle menú principal
            do {
                System.out.println("Opciones de trabajo:");
                System.out.println("1. Trabajar con libros.");
                System.out.println("2. Trabajar con autores.");
                System.out.println("3. Finalizar programa.");
                System.out.print("¿Con qué desea trabajar? ");

                // Leer entrada del usuario
                opcion = scanner.nextLine();

                // Evaluar opción usuario
                switch (opcion) {
                    case "1":
                        System.out.println("Ha elegido trabajar con libros.");
                        mostrarMenuLibros(scanner); // Llamar submenú libros
                        break;

                    case "2":
                        System.out.println("Ha elegido trabajar con autores.");
                        mostrarMenuAutores(scanner); // Llamar submenú autores
                        break;

                    case "3":
                        System.out.println("Gracias por usar el gestor. ¡Hasta pronto!");
                        scanner.close(); // Cerramos recursos
                        try {
                            conn.close();
                            System.out.println("Conexión a la base de datos cerrada.");
                        } catch (SQLException e) {
                            System.out.println(
                                    "Error al cerrar la conexión a la base de datos, por favor crea un ticket con el error para solventar el problema: "
                                            + e.getMessage());
                        }
                        return; // Salimos del programa

                    default:
                        System.out.println("Opción no válida. Por favor, intente de nuevo.\n");
                        break; // Volver a preguntar
                }
            } while (!opcion.equals("3")); // Continuar hasta que el usuario elija finalizar
        }
    }

    public static Connection connectMySQL() {
        String url = "jdbc:mysql://127.0.0.1:3306/www";
        String user = "www";
        String password = "www";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (SQLException e) {
            System.out.println(
                    "Error al conectar a la base de datos, por favor crea un ticket con el error para solventar el problema: "
                            + e.getMessage());
        }
        return conn;
    }

    // Submenú autores
    public static void mostrarMenuAutores(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Menú de Autores ---");
            System.out.println("1. Altas.");
            System.out.println("2. Bajas.");
            System.out.println("3. Modificaciones.");
            System.out.println("4. Consulta de datos.");
            System.out.println("5. Regresar a menú principal.");
            System.out.print("Seleccione una opción: ");

            // Leer opción usuario
            String opcionAutor = scanner.nextLine();

            // Evaluar opción
            switch (opcionAutor) {
                case "1":
                System.out.println("Has elegido: Altas.");

                Scanner sc = new Scanner(System.in);
                String nombre, apellido;
                
                // Validar que el nombre no esté vacío ni sea numérico
                do {
                    System.out.print("Introduce el nombre del autor: ");
                    nombre = sc.nextLine().trim();
                
                    if (nombre.isEmpty()) {
                        System.out.println("El nombre no puede estar vacío.");
                    } else if (nombre.matches("\\d+")) {
                        System.out.println("El nombre no puede ser solo números.");
                        nombre = ""; // Para repetir el ciclo
                    }
                
                } while (nombre.isEmpty());
                
                // Validar que el apellido no esté vacío ni sea numérico
                do {
                    System.out.print("Introduce el apellido del autor: ");
                    apellido = sc.nextLine().trim();
                
                    if (apellido.isEmpty()) {
                        System.out.println("El apellido no puede estar vacío.");
                    } else if (apellido.matches("\\d+")) {
                        System.out.println("El apellido no puede ser solo números.");
                        apellido = ""; // Para repetir el ciclo
                    }
                
                } while (apellido.isEmpty());
                
                // Generar cod_autor aleatorio y comprobar que no exista
                int codAutor;
                boolean codUnico = false;
                
                String url = "jdbc:mysql://localhost:3306/www";
                String usuario = "root";
                String password = "";
                
                try (Connection conn = DriverManager.getConnection(url, usuario, password)) {
                
                    do {
                        codAutor = (int)(Math.random() * 900) + 100;
                
                        // Verificar unicidad del cod_autor
                        String checkSql = "SELECT COUNT(*) FROM autores WHERE cod_autor = ?";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                            checkStmt.setInt(1, codAutor);
                            ResultSet rs = checkStmt.executeQuery();
                            if (rs.next() && rs.getInt(1) == 0) {
                                codUnico = true;
                            }
                        }
                    } while (!codUnico);
                
                    int codPais = (int)(Math.random() * 3) + 1;
                
                    // Insertar el autor
                    String sql = "INSERT INTO autores (cod_autor, nombre, apellido, cod_pais) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, codAutor);
                        stmt.setString(2, nombre);
                        stmt.setString(3, apellido);
                        stmt.setInt(4, codPais);
                
                        int filasAfectadas = stmt.executeUpdate();
                        if (filasAfectadas > 0) {
                            System.out.println("Autor agregado correctamente con ID: " + codAutor);
                        } else {
                            System.out.println("Error al agregar autor.");
                        }
                    }
                
                } catch (SQLException e) {
                    System.out.println("Error de conexión o SQL:");
                    e.printStackTrace();
                }
                


                    break; // Se queda en el submenú para seguir eligiendo

                case "2":
                    System.out.println("Has elegido: Bajas.");
                    // Aquí iría la lógica para bajas
                    break; // Se queda en el submenú para seguir eligiendo

                case "3":
                    System.out.println("Has elegido: Modificaciones.");
                    // Aquí iría la lógica para modificaciones
                    break; // Se queda en el submenú para seguir eligiendo

                case "4":
                    System.out.println("Has elegido: Consulta de datos.");
                    // Aquí iría la lógica para consultar datos
                    break; // Se queda en el submenú para seguir eligiendo

                case "5":
                    System.out.println("\nVolviendo al menú principal...\n");
                    return; // Sale del submenú y vuelve al menú principal

                default:
                    System.out.println("Opción no válida en el menú de autores. Intente nuevamente.\n");
            }
            break;
        }
    }

    // Submenú libros
    public static void mostrarMenuLibros(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Menú de Libros ---");
            System.out.println("1. Altas.");
            System.out.println("2. Bajas.");
            System.out.println("3. Modificaciones.");
            System.out.println("4. Consulta de datos.");
            System.out.println("5. Regresar a menú principal.");
            System.out.print("Seleccione una opción: ");

            // Leer opción usuario
            String opcionLibro = scanner.nextLine();

            // Evaluar opción
            switch (opcionLibro) {
                case "1":
                    System.out.println("Has elegido: Altas.");
                    // Aquí iría la lógica para altas
                    break; // Se queda en el submenú para seguir eligiendo

                case "2":
                    System.out.println("Has elegido: Bajas.");
                    // Aquí iría la lógica para bajas
                    break; // Se queda en el submenú para seguir eligiendo

                case "3":
                    System.out.println("Has elegido: Modificaciones.");
                    // Aquí iría la lógica para modificaciones
                    break; // Se queda en el submenú para seguir eligiendo

                case "4":
                    System.out.println("Has elegido: Consulta de datos.");
                    // Aquí iría la lógica para consultar datos
                    break; // Se queda en el submenú para seguir eligiendo

                case "5":
                    System.out.println("\nVolviendo al menú principal...\n");
                    return; // Sale del submenú y vuelve al menú principal

                default:
                    System.out.println("Opción no válida en el menú de libros. Intente nuevamente.\n");
            }
            break;
        }
    }
}
