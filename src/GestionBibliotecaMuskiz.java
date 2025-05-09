import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Statement;

public class GestionBibliotecaMuskiz {
    public static void main(String[] args) throws Exception {
        System.out.print("\033[H\033[2J");
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

    /// SUBMENUS ///
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
                    agregarLibro(scanner);
                    break; // Se queda en el submenú para seguir eligiendo

                case "2":
                    System.out.println("Has elegido: Bajas.");

                    String codLibroBaja = validarISBN(scanner);

                    try (Connection conn = connectMySQL()) {
                        boolean resultado = borrarLibro(conn, codLibroBaja);
                        if (resultado) {
                            System.out.println("Libro eliminado correctamente.");
                        } else {
                            System.out.println("Error al eliminar el libro.");
                        }
                    } catch (SQLException e) {
                        System.out.println("Error de conexión o SQL:");
                        e.printStackTrace();
                    }

                    break; // Se queda en el submenú para seguir eligiendo

                case "3":
                    System.out.println("Has elegido: Modificaciones.");

                    // Solicitar el ISBN del libro a modificar
                    String isbnModificar = validarISBN(scanner);

                    // Conectar a la base de datos
                    try (Connection conn = connectMySQL()) {
                        // Verificar si el libro existe
                        String checkSql = "SELECT * FROM libros WHERE isbn = ?";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                            checkStmt.setString(1, isbnModificar);
                            ResultSet rs = checkStmt.executeQuery();

                            if (!rs.next()) {
                                System.out.println("No se encontró un libro con el ISBN proporcionado.");
                                return; // Salir si no se encuentra el libro
                            }

                            // Menú de modificación
                            boolean modificar = true;
                            while (modificar) {
                                System.out.println("\n--- Menú de Modificación de Libro ---");
                                System.out.println("1. Modificar Título");
                                System.out.println("2. Modificar Número de Copias");
                                System.out.println("3. Modificar Valoración (1 a 5)");
                                System.out.println("4. Regresar al menú anterior");
                                System.out.print("Seleccione una opción: ");

                                String opcionModificar = scanner.nextLine().trim();

                                switch (opcionModificar) {
                                    case "1":
                                        // Modificar título
                                        modificarTitulo(conn, isbnModificar, scanner);
                                        break;

                                    case "2":
                                        // Modificar número de copias
                                        modificarNumeroCopias(conn, isbnModificar, scanner);
                                        break;

                                    case "3":
                                        // Modificar valoración
                                        modificarValoracion(conn, isbnModificar, scanner);
                                        break;

                                    case "4":
                                        // Regresar al menú anterior
                                        modificar = false;
                                        break;

                                    default:
                                        System.out
                                                .println("Opción no válida. Por favor, seleccione una opción válida.");
                                        break;
                                }
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("Error de conexión o SQL:");
                        e.printStackTrace();
                    }
                    break; // Se queda en el submenú para seguir eligiendo

                case "4":
                    System.out.println("Has elegido: Consulta de datos.");
                    String opcionConsulta;
                    do {
                        System.out.println("\n--- CONSULTAR LIBROS ---");
                        System.out.println("1. Todos los datos");
                        System.out.println("2. Filtrar por ISBN");
                        System.out.println("3. Filtrar por valoración");
                        System.out.print("Elige una opción: ");
                        opcionConsulta = scanner.nextLine().trim();

                        if (!opcionConsulta.equals("1") && !opcionConsulta.equals("2")) {
                            System.out.println("Opción no válida. Por favor elige del 1 al 3.");
                        }
                    } while (!opcionConsulta.equals("1") && !opcionConsulta.equals("2") && !opcionConsulta.equals("3"));

                    // Leer opción usuario
                    switch (opcionConsulta) {
                        case "1":
                            // Consulta todos los libros
                            consultarLibros(connectMySQL(), 0);
                            break;

                        case "2":
                            String isbnBuscar = validarISBN(scanner);
                            consultarLibroPorISBN(connectMySQL(), isbnBuscar);
                            break;

                        case "3":
                            int valoracionBuscar = validarValoracion(scanner);
                            consultarLibrosPorValoracion(connectMySQL(), valoracionBuscar);
                            break;

                        default:
                            break;
                    }
                    break; // Se queda en el submenú para seguir eligiendo

                case "5":
                    System.out.println("\nVolviendo al menú principal...\n");
                    continuar = false; // Cambia la variable para salir del bucle
                    return; // Sale del submenú y vuelve al menú principal

                default:
                    System.out.println("Opción no válida en el menú de libros. Intente nuevamente.\n");
            }
        }
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
                    agregarAutor(scanner);
                    break; // Se queda en el submenú para seguir eligiendo

                case "2":
                    System.out.println("Has elegido: Bajas.");
                    System.out.print("Introduce el código del autor a eliminar: ");
                    String codAutorBaja = scanner.nextLine().trim();
                    // Validar que el código no esté vacío y sea numérico
                    if (codAutorBaja.isEmpty()) {
                        System.out.println("El código no puede estar vacío.");
                    } else if (!codAutorBaja.matches("\\d+")) {
                        System.out.println("El código debe ser numérico.");
                    } else {
                        // Llamar a la función de baja
                        boolean resultado = borrarAutor(connectMySQL(), codAutorBaja);
                        if (resultado) {
                            System.out.println("Autor eliminado correctamente.");
                        } else {
                            System.out.println("Error al eliminar el autor.");
                        }
                    }

                    break; // Se queda en el submenú para seguir eligiendo

                case "3":
                    System.out.println("Has elegido: Modificaciones.");

                    String codAutorModificar = validarCodigo(scanner);

                    // Mantener la conexión abierta para el menú de modificación
                    try (Connection conn = connectMySQL()) {
                        boolean modificar = true;
                        while (modificar) {
                            System.out.println("\n--- Menú de Modificación de Autor ---");
                            System.out.println("1. Modificar Nombre");
                            System.out.println("2. Modificar Apellido");
                            System.out.println("3. Modificar Código de País");
                            System.out.println("4. Regresar al menú anterior");
                            System.out.print("Seleccione una opción: ");

                            String opcionModificar = scanner.nextLine().trim();

                            switch (opcionModificar) {
                                case "1":
                                    // Modificar nombre
                                    modificarNombre(scanner, conn, codAutorModificar, "nombre");
                                    break;

                                case "2":
                                    // Modificar apellido
                                    modificarNombre(scanner, conn, codAutorModificar, "apellido");
                                    break;

                                case "3":
                                    // Modificar código de país
                                    modificarCodigoPais(scanner, conn, codAutorModificar);

                                    break;

                                case "4":
                                    // Regresar al menú anterior
                                    modificar = false;
                                    break;

                                default:
                                    System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
                                    break;
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("Error de conexión o SQL:");
                        e.printStackTrace();
                    }

                    break; // Se queda en el submenú para seguir eligiendo

                case "4":
                    System.out.println("Has elegido: Consulta de datos.");
                    String opcionConsulta;
                    do {
                        System.out.println("\n--- CONSULTAR AUTOR ---");
                        System.out.println("1. Todos los datos");
                        System.out.println("2. Filtrar por Nombre de autor");
                        System.out.print("Elige una opción (1 o 2): ");
                        opcionConsulta = scanner.nextLine().trim();

                        if (!opcionConsulta.equals("1") && !opcionConsulta.equals("2")) {
                            System.out.println("Opción no válida. Por favor elige 1 o 2.");
                        }
                    } while (!opcionConsulta.equals("1") && !opcionConsulta.equals("2"));

                    // Leer opción usuario
                    switch (opcionConsulta) {
                        case "1":
                            // Consulta todos los libros
                            consultarAutores(connectMySQL(), 0);
                            break;

                        case "2":
                            String nombreAutor;
                            do {
                                System.out.print("Introduce el nombre del autor a buscar: ");
                                nombreAutor = scanner.nextLine().trim();

                                if (nombreAutor.isEmpty() || !nombreAutor.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")) {
                                    System.out.println("Nombre no válido. Debe contener solo letras y no estar vacío.");
                                    nombreAutor = null; // Forzar repetición
                                }
                            } while (nombreAutor == null);

                            if (!consultarAutorPorNombre(connectMySQL(), nombreAutor)) {
                                System.out.println("No se encontró el autor con el nombre: " + nombreAutor);
                            }
                            break;

                        default:
                            break;
                    }
                    break;

                case "5":
                    System.out.println("\nVolviendo al menú principal...\n");
                    continuar = false; // Cambia la variable para salir del bucle
                    break;

                default:
                    System.out.println("Opción no válida en el menú de autores. Intente nuevamente.\n");
            }
        }
    }

    /// FUNCIONES ///
    // Conexión a la base de datos
    public static Connection connectMySQL() {
        String url = "jdbc:mysql://127.0.0.1:3306/www";
        String user = "www";
        String password = "www";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            // System.out.println("Conexión exitosa a la base de datos.");
        } catch (SQLException e) {
            System.out.println(
                    "Error al conectar a la base de datos, por favor crea un ticket con el error para solventar el problema: "
                            + e.getMessage());
        }
        return conn;
    }

    // Generador de codigos aleatorios
    public static int generarCodigo(int num) {
        int cod = (int) (Math.random() * num) + 1;
        return cod;
    }

    // Alta tabla Libros
    private static void agregarLibro(Scanner scanner) {
        System.out.println("Has elegido: Altas.");

        String isbn = validarISBN(scanner);
        String titulo = validarNombre(scanner, "título del libro");
        int nCopias = validarNumeroCopias(scanner);
        int valoracion = validarValoracion(scanner);

        // Generar cod_libro aleatorio único
        int codLibro;
        boolean codUnico = false;

        try (Connection conn = connectMySQL()) {

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
                    // Llamar al método para agregar ejemplares
                    agregarEjemplares(codLibro, nCopias);
                } else {
                    System.out.println("Error al insertar el libro.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }
    }

    // Agregar ejemplares en base al libro a añadir
    private static void agregarEjemplares(int codLibro, int nCopias) {
        String sql = "INSERT INTO ejemplares (cod_ejemplar, cod_estado, cod_libro) VALUES (?, ?, ?)";
        int codEjemplar;
        boolean codUnico = false;
        try (Connection conn = connectMySQL()) {
            for (int i = 0; i < nCopias; i++) {
                do {
                    codEjemplar = generarCodigo(99999999);

                    String checkSql = "SELECT COUNT(*) FROM libros WHERE cod_libro = ?";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                        checkStmt.setInt(1, codEjemplar);
                        ResultSet rs = checkStmt.executeQuery();
                        if (rs.next() && rs.getInt(1) == 0) {
                            codUnico = true;
                        }
                    }
                } while (!codUnico);

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, codEjemplar);
                    stmt.setInt(2, 1);
                    stmt.setInt(3, codLibro);
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }
    }

    // Borrar libro (baja)
    public static boolean borrarLibro(Connection conn, String codLibroBaja) {
        String sql = "DELETE FROM libros WHERE isbn = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, codLibroBaja);
            int borrados = pst.executeUpdate();
            return (borrados > 0);
        } catch (SQLException e) {
            System.out.println("Problema al borrar:\n" + e.getMessage());
            return false;
        }
    }

    // Modificar titulo tabla Libros
    private static void modificarTitulo(Connection conn, String isbnModificar, Scanner scanner) {
        System.out.print("Introduce el nuevo título del libro: ");
        String nuevoTitulo = scanner.nextLine().trim();
        if (!nuevoTitulo.isEmpty() && !nuevoTitulo.matches("\\d+")) {
            String updateTituloSql = "UPDATE libros SET titulo = ? WHERE isbn = ?";
            try (PreparedStatement updateTituloStmt = conn.prepareStatement(updateTituloSql)) {
                updateTituloStmt.setString(1, nuevoTitulo);
                updateTituloStmt.setString(2, isbnModificar);
                int filasAfectadas = updateTituloStmt.executeUpdate();
                if (filasAfectadas > 0) {
                    System.out.println("Título actualizado correctamente.");
                } else {
                    System.out.println("Error al actualizar el título.");
                }
            } catch (SQLException e) {
                System.out.println("Error al actualizar el título: " + e.getMessage());
            }
        } else {
            System.out.println("El título no puede estar vacío y no puede ser solo numérico.");
        }
    }

    // Modificar numero de copias tabla Libros
    private static void modificarNumeroCopias(Connection conn, String isbnModificar, Scanner scanner) {
        int nCopiasModificar = validarNumeroCopias(scanner);
        String updateCopiasSql = "UPDATE libros SET n_copias = ? WHERE isbn = ?";
        try (PreparedStatement updateCopiasStmt = conn.prepareStatement(updateCopiasSql)) {
            updateCopiasStmt.setInt(1, nCopiasModificar);
            updateCopiasStmt.setString(2, isbnModificar);
            int filasAfectadas = updateCopiasStmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Número de copias actualizado correctamente.");
            } else {
                System.out.println("Error al actualizar el número de copias.");
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar el número de copias: " + e.getMessage());
        }
    }

    // Modificar valoracion tabla Libros
    private static void modificarValoracion(Connection conn, String isbnModificar, Scanner scanner) {
        int valoracionModificar = validarValoracion(scanner);
        String updateValoracionSql = "UPDATE libros SET valoracion = ? WHERE isbn = ?";
        try (PreparedStatement updateValoracionStmt = conn.prepareStatement(updateValoracionSql)) {
            updateValoracionStmt.setInt(1, valoracionModificar);
            updateValoracionStmt.setString(2, isbnModificar);
            int filasAfectadas = updateValoracionStmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Valoración actualizada correctamente.");
            } else {
                System.out.println("Error al actualizar la valoración.");
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar la valoración: " + e.getMessage());
        }
    }

    // Consultar tabla Libros
    public static void consultarLibros(Connection conn, int totalRegistros) {
        // Realizamos la consulta sql para mostrar todos los datos de la tabla
        // Muestra hasta el total de registros que le decimos
        // En caso de ser 0, muestra todo
        Statement stm = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM libros";
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

    // Consultar tabla Libros filtrando ISBN
    public static void consultarLibroPorISBN(Connection conn, String isbn) {
        String sql = "SELECT * FROM libros WHERE isbn = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Código: " + rs.getInt("cod_libro") +
                        ", ISBN: " + rs.getString("isbn") +
                        ", Título: " + rs.getString("titulo") +
                        ", Copias: " + rs.getInt("n_copias") +
                        ", Valoración: " + rs.getInt("valoracion"));
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar por ISBN: " + e.getMessage());
        }
    }

        // Consultar tabla Libros filtrando valoración
public static void consultarLibrosPorValoracion(Connection conn, int valoracionBuscar) {
    String sql = "SELECT * FROM libros WHERE valoracion = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, valoracionBuscar);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            System.out.println("Código: " + rs.getInt("cod_libro") +
                    ", ISBN: " + rs.getString("isbn") +
                    ", Título: " + rs.getString("titulo") +
                    ", Copias: " + rs.getInt("n_copias") +
                    ", Valoración: " + rs.getInt("valoracion"));
        }
    } catch (SQLException e) {
        System.out.println("Error al consultar por valoración: " + e.getMessage());
    }
}

    // Alta tabla Autores
    private static void agregarAutor(Scanner scanner) {
        System.out.println("Has elegido: Altas.");
        String nombre = validarNombre(scanner, "nombre del autor");
        String apellido = validarNombre(scanner, "apellido del autor");

        // Generar cod_autor aleatorio y comprobar que no exista
        int codAutor;
        boolean codUnico = false;

        try (Connection conn = connectMySQL()) {

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
    }

    // Borrar autor (baja)
    public static boolean borrarAutor(Connection conn, String codAutorBaja) {
        PreparedStatement pstmt = null;
        int borrados;
        String sql = "DELETE FROM autores WHERE cod_autor = ?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, codAutorBaja); // Set the value for the placeholder
            borrados = pstmt.executeUpdate();
            return (borrados > 0);
        } catch (Exception e) {
            System.out.println("Problema al borrar: " + "\n" + e.getMessage());
            return false;
        } finally {
            // Close the PreparedStatement to free resources
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    System.out.println("Error closing PreparedStatement: " + e.getMessage());
                }
            }
        }
    }

    // Modificar nombre/apellido tabla Autores
    private static void modificarNombre(Scanner scanner, Connection conn, String codAutorModificar, String tipo) {
        System.out.print("Introduce el nuevo " + tipo + " del autor: ");
        String nuevoNombre = scanner.nextLine().trim();
        if (!nuevoNombre.isEmpty()) {
            String updateNombreSql = "UPDATE autores SET " + tipo + " = ? WHERE cod_autor = ?";
            try (PreparedStatement updateNombreStmt = conn.prepareStatement(updateNombreSql)) {
                updateNombreStmt.setString(1, nuevoNombre);
                updateNombreStmt.setInt(2, Integer.parseInt(codAutorModificar));
                int filasAfectadas = updateNombreStmt.executeUpdate();
                if (filasAfectadas > 0) {
                    System.out.println("El " + tipo + " ha sido actualizado correctamente.");
                } else {
                    System.out.println("Error al actualizar el " + tipo + ".");
                }
            } catch (SQLException e) {
                System.out.println("Error al actualizar el " + tipo + ":");
                e.printStackTrace();
            }
        } else {
            System.out.println("El " + tipo + " no puede estar vacío.");
        }
    }

    // Modificar codigo pais tabla Autores
    private static void modificarCodigoPais(Scanner scanner, Connection conn, String codAutorModificar) {
        System.out.print("Introduce el nuevo código de país: ");
        String codPaisInput = scanner.nextLine().trim();
        if (!codPaisInput.isEmpty() && codPaisInput.matches("\\d+")) {
            String updatePaisSql = "UPDATE autores SET cod_pais = ? WHERE cod_autor = ?";
            try (PreparedStatement updatePaisStmt = conn.prepareStatement(updatePaisSql)) {
                updatePaisStmt.setInt(1, Integer.parseInt(codPaisInput));
                updatePaisStmt.setInt(2, Integer.parseInt(codAutorModificar));
                int filasAfectadas = updatePaisStmt.executeUpdate();
                if (filasAfectadas > 0) {
                    System.out.println("Código de país actualizado correctamente.");
                } else {
                    System.out.println("Error al actualizar el código de país.");
                }
            } catch (SQLException e) {
                System.out.println("Error al actualizar el código de país:");
                e.printStackTrace();
            }
        } else {
            System.out.println("El código de país debe ser numérico.");
        }
    }

    // Consultar tabla Autores
    public static void consultarAutores(Connection conn, int totalRegistros) {
        // Realizamos la consulta sql para mostrar todos los datos de la tabla
        // Muestra hasta el total de registros que le decimos
        // En caso de ser 0, muestra todo
        Statement stm = null;
        ResultSet rs = null;
        String sql = "SELECT * FROM autores";
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

    // Consultar tabla Autores filtrando por nombre
    public static boolean consultarAutorPorNombre(Connection conn, String nombreAutor) {
        String sql = "SELECT * FROM autores WHERE nombre LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nombreAutor + "%");
            ResultSet rs = stmt.executeQuery();
            boolean encontrado = false;

            while (rs.next()) {
                System.out.println("Código Autor: " + rs.getInt("cod_autor") +
                        ", Nombre: " + rs.getString("nombre") +
                        ", Apellido: " + rs.getString("apellido") +
                        ", País: " + rs.getInt("cod_pais"));
                encontrado = true;
            }

            if (!encontrado) {
                // No se encontró el autor
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar autor por nombre: " + e.getMessage());
        }
        return true; // Si se encontró al menos un autor
    }

    /// Validadores ///
    // Validar ISBN: exactamente 13 dígitos numéricos
    private static String validarISBN(Scanner scanner) {
        String isbn;
        do {
            System.out.print("Introduce el ISBN del libro (13 dígitos): ");
            isbn = scanner.nextLine().trim();
            if (isbn.isEmpty() || !isbn.matches("\\d{13}")) {
                System.out.println("El ISBN debe contener exactamente 13 dígitos numéricos.");
            }
        } while (isbn.isEmpty() || !isbn.matches("\\d{13}"));
        return isbn;
    }

    // Validar nombre: no vacío y no solo numérico
    private static String validarNombre(Scanner scanner, String tipo) {
        String nombre;
        do {
            System.out.print("Introduce el " + tipo + ": ");
            nombre = scanner.nextLine().trim();
            if (nombre.isEmpty() || nombre.matches("\\d+")) {
                System.out.println("El " + tipo + " no puede estar vacío y no puede ser solo números.");
            }
        } while (nombre.isEmpty() || nombre.matches("\\d+"));
        return nombre;
    }

    // Validar número de copias: numérico y positivo
    private static int validarNumeroCopias(Scanner scanner) {
        int nCopias = 0;
        do {
            System.out.print("Introduce el número de copias: ");
            String entrada = scanner.nextLine().trim();
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
        return nCopias;
    }

    // Validar valoración: entre 1 y 5
    private static int validarValoracion(Scanner scanner) {
        int valoracion = 0;
        do {
            System.out.print("Introduce la valoración (1 a 5): ");
            String entrada = scanner.nextLine().trim();
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
        return valoracion;
    }

    // Validar codigo: numerico y exista
    private static String validarCodigo(Scanner scanner) {
        String codAutorModificar = "";
        boolean autorValido = false;

        // Bucle para solicitar el código del autor hasta que sea válido
        while (!autorValido) {
            System.out.print("Introduce el código del autor a modificar: ");
            codAutorModificar = scanner.nextLine().trim();

            // Validar que el código no esté vacío y sea numérico
            if (codAutorModificar.isEmpty()) {
                System.out.println("El código no puede estar vacío.");
            } else if (!codAutorModificar.matches("\\d+")) {
                System.out.println("El código debe ser numérico.");
            } else {
                // Conectar a la base de datos
                try (Connection conn = connectMySQL()) {
                    // Verificar si el autor existe
                    String checkSql = "SELECT * FROM autores WHERE cod_autor = ?";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                        checkStmt.setInt(1, Integer.parseInt(codAutorModificar));
                        ResultSet rs = checkStmt.executeQuery();

                        if (rs.next()) {
                            autorValido = true; // Autor encontrado, salir del bucle
                        } else {
                            System.out.println(
                                    "No se encontró un autor con el código proporcionado. Inténtalo de nuevo.");
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Error de conexión o SQL:");
                    e.printStackTrace();
                }
            }
        }

        return codAutorModificar; // Retorna el código del autor si es válido
    }
}
