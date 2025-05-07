import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Statement;

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

    public static int generarCodigo(int num) {
        int cod = (int) (Math.random() * num) + 1;
        return cod;
    }

    // Submenú autores
    public static void mostrarMenuAutores(Scanner scanner) {
        boolean continuar = true; // Variable para controlar el bucle

        while (continuar) {
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
                    String usuario = "www";
                    String password = "www";

                    try (Connection conn = DriverManager.getConnection(url, usuario, password)) {

                        do {
                            codAutor = generarCodigo(99999);

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

                        int codPais = generarCodigo(3);

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
                    System.out.print("Introduce el código del autor a eliminar: ");
                    String codAutorBaja = scanner.nextLine().trim();
                    // Validar que el código no esté vacío y sea numérico
                    if (codAutorBaja.isEmpty()) {
                        System.out.println("El código no puede estar vacío.");
                    } else if (!codAutorBaja.matches("\\d+")) {
                        System.out.println("El código debe ser numérico.");
                    } else {
                        // Llamar a la función de baja
                        boolean resultado = borrarUsuario(connectMySQL(), codAutorBaja);
                        if (resultado) {
                            System.out.println("Autor eliminado correctamente.");
                        } else {
                            System.out.println("Error al eliminar el autor.");
                        }
                    }

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
                    continuar = false; // Cambia la variable para salir del bucle
                    break;

                default:
                    System.out.println("Opción no válida en el menú de autores. Intente nuevamente.\n");
            }
        }
    }

    // Submenú libros
    public static void mostrarMenuLibros(Scanner scanner) {
        boolean continuar = true; // Variable para controlar el bucle
        while (continuar) {
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
                    Scanner sc = new Scanner(System.in);
                    String isbn, titulo;
                    int nCopias = 0, valoracion = 0;

                    // Validar ISBN: exactamente 13 dígitos numéricos
                    do {
                        System.out.print("Introduce el ISBN del libro (13 dígitos): ");
                        isbn = sc.nextLine().trim();
                        if (isbn.isEmpty()) {
                            System.out.println("El ISBN no puede estar vacío.");
                        } else if (!isbn.matches("\\d{13}")) {
                            System.out.println("El ISBN debe contener exactamente 13 dígitos numéricos sin guiones.");
                            isbn = "";
                        }
                    } while (isbn.isEmpty());

                    // Validar título: no vacío y no solo numérico
                    do {
                        System.out.print("Introduce el título del libro: ");
                        titulo = sc.nextLine().trim();
                        if (titulo.isEmpty()) {
                            System.out.println("El título no puede estar vacío.");
                        } else if (titulo.matches("\\d+")) {
                            System.out.println("El título no puede ser solo números.");
                            titulo = "";
                        }
                    } while (titulo.isEmpty());

                    // Validar número de copias: numérico y positivo
                    do {
                        System.out.print("Introduce el número de copias: ");
                        String entrada = sc.nextLine().trim();
                        try {
                            nCopias = Integer.parseInt(entrada);
                            if (nCopias < 1) {
                                System.out.println("Debe haber al menos una copia.");
                                nCopias = 0;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Ingresa un número válido.");
                        }
                    } while (nCopias == 0);

                    // Validar valoración: entre 1 y 5
                    do {
                        System.out.print("Introduce la valoración (1 a 5): ");
                        String entrada = sc.nextLine().trim();
                        try {
                            valoracion = Integer.parseInt(entrada);
                            if (valoracion < 1 || valoracion > 5) {
                                System.out.println("La valoración debe estar entre 1 y 5.");
                                valoracion = 0;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Ingresa un número válido.");
                        }
                    } while (valoracion == 0);

                    // Generar cod_libro aleatorio único
                    int codLibro;
                    boolean codUnico = false;

                    String url = "jdbc:mysql://localhost:3306/www";
                    String usuario = "www";
                    String password = "www";

                    try (Connection conn = DriverManager.getConnection(url, usuario, password)) {

                        do {
                            codLibro = generarCodigo(99999999);

                            String checkSql = "SELECT COUNT(*) FROM libros WHERE cod_libro = ?";
                            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                                checkStmt.setInt(1, codLibro);
                                ResultSet rs = checkStmt.executeQuery();
                                if (rs.next() && rs.getInt(1) == 0) {
                                    codUnico = true;
                                }
                            }
                        } while (!codUnico);

                        // Insertar libro
                        String sql = "INSERT INTO libros (cod_libro, isbn, titulo, n_copias, valoracion) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setInt(1, codLibro);
                            stmt.setString(2, isbn);
                            stmt.setString(3, titulo);
                            stmt.setInt(4, nCopias);
                            stmt.setInt(5, valoracion);

                            int filas = stmt.executeUpdate();
                            if (filas > 0) {
                                System.out.println("Libro agregado correctamente con código: " + codLibro);
                            } else {
                                System.out.println("Error al insertar el libro.");
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
                    continuar = false; // Cambia la variable para salir del bucle
                    return; // Sale del submenú y vuelve al menú principal

                default:
                    System.out.println("Opción no válida en el menú de libros. Intente nuevamente.\n");
            }
            break;
        }
    }

    /// FUNCIONES ///

    // Consultar tabla Libros
    public static void consultarLibros(Connection conn, int totalRegistros) {
        // Realizamos la consulta sql para mostrar todos los datos de la tabla
        // Muestra hasta el total de registros que le decimos
        // En caso de ser 0, muestra todo
        Statement stm = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM LIBROS";
        String cod_libro, isbn, titulo;

        try {
            stm = conn.createStatement();
            rs = stm.executeQuery(sql);
            System.out.println("TODOS LOS REGISTROS DE LA TABLA LIBROS: \n");
            int cont = 0;
            while (rs.next()) {
                cod_libro = rs.getString("cod_libro");
                isbn = rs.getString("ISBN");
                titulo = rs.getString("titulo");
                System.out.println(cod_libro + " | " + isbn + " | " + titulo);
                if (cont > totalRegistros && totalRegistros != 0)
                    break;
            }
        } catch (SQLException e) {
            System.out
                    .println("\nProblema al consultar: " + "\n" + sql + "\n" + e.getErrorCode() + " " + e.getMessage());
        }
    }

    // Consultar tabla Autores
    public static void consultarAutores(Connection conn, int totalRegistros) {
        // Realizamos la consulta sql para mostrar todos los datos de la tabla
        // Muestra hasta el total de registros que le decimos
        // En caso de ser 0, muestra todo
        Statement stm = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM AUTORES";
        String cod_autor, nombre, apellido;

        try {
            stm = conn.createStatement();
            rs = stm.executeQuery(sql);
            System.out.println("TODOS LOS REGISTROS DE LA TABLA AUTORES: \n");
            int cont = 0;
            while (rs.next()) {
                cod_autor = rs.getString("cod_autor");
                nombre = rs.getString("nombre");
                apellido = rs.getString("apellido");
                System.out.println(cod_autor + " | " + nombre + " | " + apellido);
                if (cont > totalRegistros && totalRegistros != 0)
                    break;
            }
        } catch (SQLException e) {
            System.out
                    .println("\nProblema al consultar: " + "\n" + sql + "\n" + e.getErrorCode() + " " + e.getMessage());
        }
    }

    // Borrar usuario (baja)
    public static boolean borrarUsuario(Connection conn, String codAutorBaja) {
        Statement st;
        int borrados;
        String sql = "DELETE FROM USUARIOS WHERE cod_autor= ?";
        try {
            st = conn.createStatement();
            borrados = st.executeUpdate(sql);
            return (borrados > 0);
        } catch (Exception e) {
            System.out
                    .println("Problema al borrar: " + "\n" + ((SQLException) e).getErrorCode() + " " + e.getMessage());
            return false;
        }
    }
}
