import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.sql.Statement;

public class GestionBibliotecaMuskiz {
    public static void main(String[] args) throws Exception {
        // Limpiar la consola
        System.out.print("\033[H\033[2J");

        // Establecer conexión a la base de datos MySQL
        Connection conn = connectMySQL();

        // Verificar si la conexión fue exitosa
        if (conn != null) {

            // Crear objeto scanner
            // Crear objeto scanner para leer la entrada del usuario
            Scanner scanner = new Scanner(System.in);
            String opcion = ""; // Variable para la opción del menú principal

            // Encabezado del programa
            System.out.println("=========================================================");
            System.out.println("=============== Gestión Biblioteca Muskiz ===============");
            System.out.println("=========================================================");
            System.out.println("Bienvenido al programa Gestor de la Biblioteca de Muskiz");

            // Bucle para mostrar el menú principal
            do {
                // Mostrar opciones de trabajo
                System.out.println("Opciones de trabajo:");
                System.out.println("1. Trabajar con libros.");
                System.out.println("2. Trabajar con autores.");
                System.out.println("3. Trabajar con préstamos.");
                System.out.println("4. Trabajar con socios.");
                System.out.println("5. Finalizar programa.");
                System.out.print("Seleccione una opción: ");

                // Leer la entrada del usuario
                opcion = scanner.nextLine();

                // Evaluar la opción seleccionada por el usuario
                switch (opcion) {
                    case "1":
                        // Opción para trabajar con libros
                        System.out.println("Ha elegido trabajar con libros.");
                        mostrarMenuLibros(scanner); // Llamar submenú libros
                        break;

                    case "2":
                        // Opción para trabajar con autores
                        System.out.println("Ha elegido trabajar con autores.");
                        mostrarMenuAutores(scanner); // Llamar submenú autores
                        break;

                    case "3":
                        // Opción para trabajar con préstamos
                        System.out.println("Ha elegido trabajar con préstamos.");
                        mostrarMenuPrestamos(scanner); // Llamar submenú préstamos
                        break;

                    case "4":
                        // Opción para trabajar con socios
                        System.out.println("Ha elegido trabajar con socios.");
                        mostrarMenuSocios(scanner); // Llamar submenú préstamos
                        break;

                    case "5":
                        // Opción para finalizar el programa
                        System.out.println("Gracias por usar el gestor. ¡Hasta pronto!");
                        scanner.close(); // Cerrar el objeto scanner para liberar recursos
                        try {
                            conn.close(); // Cerrar la conexión a la base de datos
                            // System.out.println("Conexión a la base de datos cerrada.");
                        } catch (SQLException e) {
                            // Manejar excepciones al cerrar la conexión
                            System.out.println(
                                    "Error al cerrar la conexión a la base de datos, por favor crea un ticket con el error para solventar el problema: "
                                            + e.getMessage());
                        }
                        break;

                    default:
                        // Opción no válida, solicitar al usuario que intente de nuevo
                        System.out.println("Opción no válida. Por favor, intente de nuevo.\n");
                        break; // Volver a preguntar
                }
            } while (!opcion.equals("5")); // Continuar el bucle hasta que el usuario elija finalizar
        }
    }

    /// SUBMENUS ///
    // Submenú libros
    public static void mostrarMenuLibros(Scanner scanner) {
        boolean continuar = true; // Variable para controlar el bucle del submenú
        while (continuar) {
            // Mostrar opciones del menú de libros
            System.out.println("\n--- Menú de Libros ---");
            System.out.println("1. Altas.");
            System.out.println("2. Bajas.");
            System.out.println("3. Modificaciones.");
            System.out.println("4. Consulta de datos.");
            System.out.println("5. Regresar a menú principal.");
            System.out.print("Seleccione una opción: ");

            // Leer la opción seleccionada por el usuario
            String opcionLibro = scanner.nextLine();

            // Evaluar la opción seleccionada
            switch (opcionLibro) {
                case "1":
                    // Opción para agregar un nuevo libro
                    agregarLibro(scanner);
                    break; // Se queda en el submenú para seguir eligiendo

                case "2":
                    // Opción para eliminar un libro
                    System.out.println("Has elegido: Bajas.");

                    System.out.print("Introduce el ISBN del libro (13 dígitos): ");
                    String isbn = scanner.nextLine().trim(); // Leer el ISBN del libro a eliminar
                    // Validar el formato del ISBN
                    if (isbn.isEmpty() || !isbn.matches("\\d{13}")) {
                        System.out.println("El ISBN debe contener exactamente 13 dígitos numéricos.");
                    } else {
                        // Llamar al método para borrar el libro
                        borrarLibro(connectMySQL(), isbn);
                    }

                    break; // Se queda en el submenú para seguir eligiendo

                case "3":
                    // Opción para modificar un libro existente
                    System.out.println("Has elegido: Modificaciones.");

                    System.out.print("Introduce el ISBN del libro (13 dígitos): ");
                    String isbnModificar = scanner.nextLine().trim(); // Leer el ISBN del libro a modificar
                    // Validar el formato del ISBN
                    if (isbnModificar.isEmpty() || !isbnModificar.matches("\\d{13}")) {
                        System.out.println("El ISBN debe contener exactamente 13 dígitos numéricos.");
                        continue;
                    }

                    // Conectar a la base de datos
                    try (Connection conn = connectMySQL()) {
                        // Verificar si el libro existe en la base de datos
                        String checkSql = "SELECT * FROM libros WHERE isbn = ?";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                            checkStmt.setString(1, isbnModificar); // Establecer el ISBN en la consulta
                            ResultSet rs = checkStmt.executeQuery(); // Ejecutar la consulta

                            // Comprobar si se encontró el libro
                            if (!rs.next()) {
                                System.out.println("No se encontró un libro con el ISBN proporcionado.");
                                continue; // Salir si no se encuentra el libro
                            }

                            // Menú de modificación
                            boolean modificar = true; // Variable para controlar el bucle de modificación
                            while (modificar) {
                                // Mostrar opciones de modificación
                                System.out.println("\n--- Menú de Modificación de Libro ---");
                                System.out.println("1. Modificar Título");
                                System.out.println("2. Modificar Número de Copias");
                                System.out.println("3. Modificar Valoración (1 a 5)");
                                System.out.println("4. Regresar al menú anterior");
                                System.out.print("Seleccione una opción: ");

                                // Leer la opción de modificación
                                String opcionModificar = scanner.nextLine().trim();

                                // Evaluar la opción seleccionada
                                switch (opcionModificar) {
                                    case "1":
                                        // Modificar título del libro
                                        modificarTitulo(conn, isbnModificar, scanner);
                                        break;

                                    case "2":
                                        // Modificar número de copias del libro
                                        modificarNumeroCopias(conn, isbnModificar, scanner);
                                        break;

                                    case "3":
                                        // Modificar valoración del libro
                                        modificarValoracion(conn, isbnModificar, scanner);
                                        break;

                                    case "4":
                                        // Regresar al menú anterior
                                        modificar = false; // Cambiar la variable para salir del bucle
                                        break;

                                    default:
                                        // Opción no válida
                                        System.out
                                                .println("Opción no válida. Por favor, seleccione una opción válida.");
                                        break;
                                }
                            }
                        }
                    } catch (SQLException e) {
                        // Manejar excepciones de conexión o SQL
                        System.out.println("Error de conexión o SQL:");
                        e.printStackTrace(); // Imprimir el stack trace para depuración
                    }
                    break; // Se queda en el submenú para seguir eligiendo

                case "4":
                    // Opción para consultar datos de libros
                    System.out.println("Has elegido: Consulta de datos.");
                    String opcionConsulta;
                    do {
                        // Mostrar opciones de consulta
                        System.out.println("\n--- CONSULTAR LIBROS ---");
                        System.out.println("1. Todos los datos");
                        System.out.println("2. Filtrar por ISBN");
                        System.out.println("3. Filtrar por valoración");
                        System.out.print("Elige una opción: ");
                        opcionConsulta = scanner.nextLine().trim(); // Leer la opción de consulta

                        // Validar la opción seleccionada
                        if (!opcionConsulta.equals("1") && !opcionConsulta.equals("2")) {
                            System.out.println("Opción no válida. Por favor elige del 1 al 3.");
                        }
                    } while (!opcionConsulta.equals("1") && !opcionConsulta.equals("2") && !opcionConsulta.equals("3"));

                    // Leer la opción seleccionada y realizar la consulta correspondiente
                    switch (opcionConsulta) {
                        case "1":
                            // Consulta todos los libros
                            consultarLibros(connectMySQL(), 0);
                            break;

                        case "2":
                            // Consultar libro por ISBN
                            String isbnBuscar = validarISBN(scanner, false); // Validar el ISBN ingresado
                            consultarLibroPorISBN(connectMySQL(), isbnBuscar);
                            break;

                        case "3":
                            // Consultar libros por valoración
                            int valoracionBuscar = validarValoracion(scanner); // Validar la valoración ingresada
                            consultarLibrosPorValoracion(connectMySQL(), valoracionBuscar);
                            break;

                        default:
                            // No se espera llegar aquí, pero se incluye para completar el switch
                            break;
                    }
                    break; // Se queda en el submenú para seguir eligiendo

                case "5":
                    // Opción para regresar al menú principal
                    System.out.println("\nVolviendo al menú principal...\n");
                    continuar = false; // Cambia la variable para salir del bucle
                    return; // Sale del submenú y vuelve al menú principal

                default:
                    // Opción no válida en el menú de libros
                    System.out.println("Opción no válida en el menú de libros. Intente nuevamente.\n");
            }
        }
    }

    // Submenú autores
    public static void mostrarMenuAutores(Scanner scanner) {
        boolean continuar = true; // Variable para controlar el bucle del submenú

        while (continuar) {
            // Mostrar opciones del menú de autores
            System.out.println("\n--- Menú de Autores ---");
            System.out.println("1. Altas.");
            System.out.println("2. Bajas.");
            System.out.println("3. Modificaciones.");
            System.out.println("4. Consulta de datos.");
            System.out.println("5. Regresar a menú principal.");
            System.out.print("Seleccione una opción: ");

            // Leer la opción seleccionada por el usuario
            String opcionAutor = scanner.nextLine();

            // Evaluar la opción seleccionada
            switch (opcionAutor) {
                case "1":
                    // Opción para agregar un nuevo autor
                    agregarAutor(scanner);
                    break; // Se queda en el submenú para seguir eligiendo

                case "2":
                    // Opción para eliminar un autor
                    System.out.println("Has elegido: Bajas.");
                    System.out.print("Introduce el código del autor a eliminar: ");
                    String codAutorBaja = scanner.nextLine().trim(); // Leer el código del autor a eliminar
                    // Validar que el código no esté vacío y sea numérico
                    if (codAutorBaja.isEmpty()) {
                        System.out.println("El código no puede estar vacío.");
                    } else if (!codAutorBaja.matches("\\d+")) {
                        System.out.println("El código debe ser numérico.");
                    } else {
                        // Llamar a la función de baja
                        borrarAutor(connectMySQL(), codAutorBaja);
                    }

                    break; // Se queda en el submenú para seguir eligiendo

                case "3":
                    // Opción para modificar un autor existente
                    System.out.println("Has elegido: Modificaciones.");

                    System.out.print("Introduce el código del autor a eliminar: ");
                    String codAutorModificar = scanner.nextLine().trim(); // Leer el código del autor a modificar
                    // Validar que el código no esté vacío y sea numérico
                    if (codAutorModificar.isEmpty()) {
                        System.out.println("El código no puede estar vacío.");
                        continue;
                    } else if (!codAutorModificar.matches("\\d+")) {
                        System.out.println("El código debe ser numérico.");
                        continue;
                    } else {
                        // Conectar a la base de datos
                        try (Connection conn = connectMySQL()) {
                            // Verificar si el autor existe en la base de datos
                            String checkSql = "SELECT * FROM autores WHERE cod_autor = ?";
                            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                                // Establecer el código en la consulta
                                checkStmt.setInt(1, Integer.parseInt(codAutorModificar));
                                ResultSet rs = checkStmt.executeQuery(); // Ejecutar la consulta

                                // Comprobar si se encontró el autor
                                if (!rs.next()) {
                                    System.out.println(
                                            "No se encontró un autor con el código proporcionado. Inténtalo de nuevo.");
                                    continue;
                                }
                            }
                        } catch (SQLException e) {
                            // Manejar excepciones de conexión o SQL
                            System.out.println("Error de conexión o SQL:");
                            e.printStackTrace(); // Imprimir el stack trace para depuración
                            continue;
                        }
                    }

                    // Mantener la conexión abierta para el menú de modificación
                    try (Connection conn = connectMySQL()) {
                        boolean modificar = true; // Variable para controlar el bucle de modificación
                        while (modificar) {
                            // Mostrar opciones de modificación
                            System.out.println("\n--- Menú de Modificación de Autor ---");
                            System.out.println("1. Modificar Nombre");
                            System.out.println("2. Modificar Apellido");
                            System.out.println("3. Modificar Código de País");
                            System.out.println("4. Regresar al menú anterior");
                            System.out.print("Seleccione una opción: ");

                            String opcionModificar = scanner.nextLine().trim(); // Leer la opción de modificación

                            // Evaluar la opción seleccionada
                            switch (opcionModificar) {
                                case "1":
                                    // Modificar el nombre del autor
                                    modificarNombre(scanner, conn, codAutorModificar, "nombre");
                                    break;

                                case "2":
                                    // Modificar el apellido del autor
                                    modificarNombre(scanner, conn, codAutorModificar, "apellido");
                                    break;

                                case "3":
                                    // Modificar el código de país del autor
                                    modificarCodigoPais(scanner, conn, codAutorModificar);

                                    break;

                                case "4":
                                    // Regresar al menú anterior
                                    modificar = false; // Cambiar la variable para salir del bucle
                                    break;

                                default:
                                    // Opción no válida
                                    System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
                                    break;
                            }
                        }
                    } catch (SQLException e) {
                        // Manejar excepciones de conexión o SQL
                        System.out.println("Error de conexión o SQL:");
                        e.printStackTrace(); // Imprimir el stack trace para depuración
                    }

                    break; // Se queda en el submenú para seguir eligiendo

                case "4":
                    // Opción para consultar datos de autores
                    System.out.println("Has elegido: Consulta de datos.");
                    String opcionConsulta;
                    do {
                        // Mostrar opciones de consulta
                        System.out.println("\n--- Consultar Autor ---");
                        System.out.println("1. Todos los datos");
                        System.out.println("2. Filtrar por Nombre de autor");
                        System.out.print("Elige una opción (1 o 2): ");
                        opcionConsulta = scanner.nextLine().trim(); // Leer la opción de consulta

                        // Validar la opción seleccionada
                        if (!opcionConsulta.equals("1") && !opcionConsulta.equals("2")) {
                            System.out.println("Opción no válida. Por favor elige 1 o 2.");
                        }
                    } while (!opcionConsulta.equals("1") && !opcionConsulta.equals("2"));

                    // Leer la opción seleccionada y realizar la consulta correspondiente
                    switch (opcionConsulta) {
                        case "1":
                            // Consulta todos los autores
                            consultarAutores(connectMySQL(), 0);
                            break;

                        case "2":
                            String nombreAutor;
                            do {
                                System.out.print("Introduce el nombre del autor a buscar: ");
                                nombreAutor = scanner.nextLine().trim(); // Leer el nombre del autor a buscar

                                // Validar que el nombre no esté vacío y contenga solo letras
                                if (nombreAutor.isEmpty() || !nombreAutor.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$")) {
                                    System.out.println("Nombre no válido. Debe contener solo letras y no estar vacío.");
                                    nombreAutor = null; // Forzar repetición
                                }
                            } while (nombreAutor == null); // Repetir hasta que se ingrese un nombre válido

                            // Consultar autor por nombre
                            if (!consultarAutorPorNombre(connectMySQL(), nombreAutor)) {
                                System.out.println("No se encontró el autor con el nombre: " + nombreAutor);
                            }
                            break;

                        default:
                            // No se espera llegar aquí, pero se incluye para completar el switch
                            break;
                    }
                    break;

                case "5":
                    // Opción para regresar al menú principal
                    System.out.println("\nVolviendo al menú principal...\n");
                    continuar = false; // Cambiar la variable para salir del bucle
                    break;

                default:
                    // Opción no válida en el menú de autores
                    System.out.println("Opción no válida en el menú de autores. Intente nuevamente.\n");
            }
        }
    }

    // Submenú préstamos
    public static void mostrarMenuPrestamos(Scanner scanner) {
        // Validar inicio de sesión del usuario
        int codUsuario = validarInicioSesion(scanner);
        if (codUsuario == -1) {
            // Mensaje de error si no se encuentra el usuario
            System.out.println("No se encontró usuario con nombre y contraseña proporcionados. Inténtelo de nuevo.");
            return; // Salir del método si el inicio de sesión falla
        }

        // Comprobar el nivel de penalización y los días restantes
        int nivelPenalizacion = comprobarPenalizacion(codUsuario);
        long diasPenalizacion = diasPenalizacionRestantes(codUsuario);
        boolean continuar = true; // Variable para controlar el bucle del menú

        while (continuar) {
            // Mostrar información del usuario
            System.out.println("\n--- Datos del Usuario ---");
            System.out.println("- Código de usuario: " + codUsuario);
            System.out.println("- Nivel de penalización: " + nivelPenalizacion);
            System.out.println("- Dias de penalización: " + diasPenalizacion);
            System.out.println("\n--- Menú de Préstamos ---");
            System.out.println("1. Prestar libro.");
            System.out.println("2. Devolver libro.");
            System.out.println("3. Consultar tus préstamos.");
            System.out.println("4. Consultar disponibilidad de libros.");
            System.out.println("5. Regresar al menú principal.");
            System.out.println("Seleccione una opción:");

            // Leer la opción seleccionada por el usuario
            String opcionPrestamo = scanner.nextLine();

            // Evaluar la opción seleccionada
            switch (opcionPrestamo) {
                case "1":
                    // Opción para prestar un libro
                    System.out.println("Has elegido: Prestar Libro.");

                    // Validar si el usuario puede realizar un préstamo
                    if (validarRealizarPrestamo(codUsuario)) {
                        // Pedir el ISBN del libro y comprobar si está disponible
                        System.out.print("Introduce el ISBN del libro (13 dígitos): ");
                        String isbnPrestamo = scanner.nextLine().trim();
                        // Validar el formato del ISBN
                        if (isbnPrestamo.isEmpty() || !isbnPrestamo.matches("\\d{13}")) {
                            System.out.println("El ISBN debe contener exactamente 13 dígitos numéricos.");
                        } else if (isbnYaExiste(isbnPrestamo)) {
                            // Si el ISBN existe, obtener el código del libro y realizar el préstamo
                            int codLibro = obtenerCodLibroPorIsbn(isbnPrestamo);
                            realizarPrestamo(codLibro, codUsuario);
                        } else {
                            // Mensaje si el ISBN no existe
                            System.out.println("No existe el ISBN indicado.");
                        }
                    } else {
                        // Mensaje si el usuario ha alcanzado el límite de libros permitidos
                        System.out.println("El usuario ha alcanzado el límite de libros permitidos.");
                    }
                    break;

                case "2":
                    // Opción para devolver un libro
                    System.out.println("Has elegido: Devolver Libro.");

                    // Variable para controlar el bucle del menú de devoluciones
                    boolean menuDevolucion = true;

                    // Bucle para el menú de devoluciones
                    while (menuDevolucion) {
                        System.out.println("\n--- Menú de Devoluciones ---");
                        System.out.println("1. Devolver un libro prestado.");
                        System.out.println("2. Devolver todos los libros prestados.");
                        System.out.println("3. Regresar al menú anterior");
                        System.out.print("Seleccione una opción: ");

                        // Leer la opción seleccionada
                        String opcionConsultar = scanner.nextLine().trim();

                        switch (opcionConsultar) {
                            case "1":
                                // Realizar devolución de un libro
                                System.out.print("Introduce el ISBN del libro (13 dígitos): ");
                                String isbnDevolver = scanner.nextLine().trim();
                                // Validar el formato del ISBN
                                if (isbnDevolver.isEmpty() || !isbnDevolver.matches("\\d{13}")) {
                                    System.out.println("El ISBN debe contener exactamente 13 dígitos numéricos.");
                                    continue;
                                }
                                // Obtener el código del libro a devolver
                                int codLibroDevolver = obtenerCodLibroPorIsbn(isbnDevolver);
                                // Verificar si se encontró el libro
                                if (codLibroDevolver == -1) {
                                    System.out.println("No se encontró el libro con ISBN: " + codLibroDevolver);
                                } else {
                                    // Realizar la devolución del libro
                                    realizarDevolucion(connectMySQL(), codUsuario, codLibroDevolver);
                                }
                                break;

                            case "2":
                                // Realizar todas las devoluciones del usuario
                                realizarDevolucion(connectMySQL(), codUsuario);
                                break;

                            case "3":
                                // Regresar al menú anterior
                                menuDevolucion = false;
                                break;

                            default:
                                System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
                                break;
                        }
                    }

                    break;

                case "3":
                    // Opción para consultar los préstamos del usuario
                    System.out.println("Has elegido: Consultar tus Préstamos.");
                    consultarPrestamosUsuario(connectMySQL(), codUsuario);
                    break;

                case "4":
                    // Opción para consultar la disponibilidad de libros
                    System.out.println("Has elegido: Consultar Disponibilidad de Libros.");
                    boolean consultaDisponibilidad = true;

                    // Bucle para el menú de consulta de disponibilidad
                    while (consultaDisponibilidad) {
                        System.out.println("\n--- Menú de Consulta de Disponibilidad ---");
                        System.out.println("1. Consultar disponibilidad de todos los libros.");
                        System.out.println("2. Consultar disponibilidad de un libro (Filtrado por ISBN).");
                        System.out.println("3. Consultar disponibilidad de un libro (Filtrado por titulo).");
                        System.out.println("4. Regresar al menú anterior.");
                        System.out.print("Seleccione una opción: ");

                        String opcionConsultar = scanner.nextLine().trim(); // Leer la opción seleccionada

                        switch (opcionConsultar) {
                            case "1":
                                // Consultar disponibilidad de todos los libros
                                consultarDisponibilidadLibros(connectMySQL());
                                break;

                            case "2":
                                // Consultar disponibilidad de un libro por ISBN
                                System.out.print("Introduce el ISBN del libro (13 dígitos): ");
                                String isbn = scanner.nextLine().trim();
                                if (isbn.isEmpty() || !isbn.matches("\\d{13}")) {
                                    System.out.println("El ISBN debe contener exactamente 13 dígitos numéricos.");
                                } else {
                                    consultarDisponibilidadLibrosFiltrado(connectMySQL(), "isbn", isbn);
                                }
                                break;

                            case "3":
                                // Consultar disponibilidad de un libro por título
                                System.out.print("Introduce el titulo del libro: ");
                                String titulo = scanner.nextLine().trim();
                                if (titulo.isEmpty() || titulo.matches("\\d+")) {
                                    System.out.println("El titulo no puede estar vacío y no puede ser solo números.");
                                } else {
                                    consultarDisponibilidadLibrosFiltrado(connectMySQL(), "titulo", titulo);
                                }
                                break;

                            case "4":
                                // Regresar al menú anterior
                                consultaDisponibilidad = false;
                                break;

                            default:
                                // Opción no válida
                                System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
                                break;
                        }
                    }

                    break;

                case "5":
                    // Opción para regresar al menú principal
                    System.out.println("\nVolviendo al menú principal...\n");
                    continuar = false; // Cambia la variable para salir del bucle
                    break;

                default:
                    // Opción no válida en el menú de préstamos
                    System.out.println("Opción no válida en el menú de préstamos. Intente nuevamente.\n");
            }
        }

    }

    // Submenú socio
    public static void mostrarMenuSocios(Scanner scanner) {
        boolean continuar = true; // Variable para controlar el bucle

        // Bucle principal del menú de socios
        while (continuar) {
            System.out.println("\n--- Menú de Socios ---");
            System.out.println("1. Añadir socio.");
            System.out.println("2. Borrar socio.");
            System.out.println("3. Modificar socio.");
            System.out.println("4. Consultar socio.");
            System.out.println("5. Regresar al menú principal.");
            System.out.println("Seleccione una opción:");

            // Leer opción usuario
            String opcionUsuario = scanner.nextLine();

            // Evaluar opción
            switch (opcionUsuario) {
                case "1":
                    // Opción para añadir un nuevo socio
                    System.out.println("Has elegido: Añadir socio.");
                    agregarSocio(scanner);
                    break;

                case "2":
                    // Opción para borrar un socio
                    System.out.println("Has elegido: Borrar socio.");
                    String dniBaja = "";
                    boolean dniValido = false;

                    // Bucle para validar el DNI del socio a eliminar
                    do {
                        System.out.print("Introduce el DNI del socio a eliminar: ");
                        dniBaja = scanner.nextLine().trim().toUpperCase();

                        if (dniBaja.isEmpty()) {
                            System.out.println("El DNI no puede estar vacío. Inténtalo de nuevo.");
                        } else if (!dniBaja.matches("\\d{8}[A-Z]")) {
                            System.out.println(
                                    "Formato de DNI inválido. Debe tener 8 números seguidos de una letra (ej: 12345678A).");
                        } else {
                            dniValido = true;
                        }
                    } while (!dniValido);

                    // Llamar a la función de baja con el DNI validado
                    borrarSocioPorDNI(connectMySQL(), dniBaja);

                    break;

                case "3":
                    // Opción para modificar un socio
                    System.out.println("Has elegido: Modificar socio.");
                    // 1. Pedir y validar el DNI
                    String dniMod = validarDNI(scanner);

                    // 2. Conectar a la base de datos y verificar si existe el socio
                    try (Connection conn = connectMySQL()) {
                        String checkSql = "SELECT * FROM usuarios WHERE dni = ?";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                            checkStmt.setString(1, dniMod);
                            ResultSet rs = checkStmt.executeQuery();

                            if (!rs.next()) {
                                System.out.println("No se encontró ningún socio con ese DNI.");
                                break;
                            }
                        }

                        // 3. Pedir nuevos valores válidos para los campos modificables
                        String nuevoNombre = validarNombre(scanner, "nombre del socio");
                        String nuevoTelefono = validarTelefono(scanner);
                        String nuevoCorreo = validarCorreo(scanner);
                        String nuevaContrasena = validarContraseña(scanner);

                        // 4. Ejecutar la actualización
                        String updateSql = "UPDATE usuarios SET nombre = ?, telefono = ?, correo = ?, contraseña = ? WHERE dni = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, nuevoNombre);
                            updateStmt.setString(2, nuevoTelefono);
                            updateStmt.setString(3, nuevoCorreo);
                            updateStmt.setString(4, nuevaContrasena);
                            updateStmt.setString(5, dniMod);

                            int filas = updateStmt.executeUpdate();
                            if (filas > 0) {
                                System.out.println("Socio modificado correctamente.");
                            } else {
                                System.out.println("No se pudo modificar el socio.");
                            }
                        }

                    } catch (SQLException e) {
                        System.out.println("Error al modificar el socio:");
                        e.printStackTrace();
                    }

                    break;

                case "4":
                    // Opción para consultar un socio
                    System.out.println("Has elegido: Consultar socio.");
                    String opcionConsulta;
                    do {
                        System.out.println("\n--- Consultar Socio ---");
                        System.out.println("1. Todos los datos");
                        System.out.println("2. Buscar por DNI del socio");
                        System.out.print("Elige una opción (1 o 2): ");
                        opcionConsulta = scanner.nextLine().trim();

                        if (!opcionConsulta.equals("1") && !opcionConsulta.equals("2")) {
                            System.out.println("Opción no válida. Por favor elige 1 o 2.");
                        }
                    } while (!opcionConsulta.equals("1") && !opcionConsulta.equals("2"));

                    switch (opcionConsulta) {
                        case "1":
                            // Consulta todos los socios
                            consultarSocios(connectMySQL());
                            break;

                        case "2":
                            // Pedir DNI válido
                            String dniConsulta = validarDNI(scanner);

                            // Llamar a función de consulta por DNI
                            if (!consultarSocioPorDNI(connectMySQL(), dniConsulta)) {
                                System.out.println("No se encontró ningún socio con el DNI: " + dniConsulta);
                            }
                            break;
                    }

                    break;

                case "5":
                    // Mostrar opciones de consulta
                    System.out.println("\nVolviendo al menú principal...\n");
                    continuar = false; // Cambia la variable para salir del bucle
                    break;

                default:
                    // Manejo de opción no válida
                    System.out.println("Opción no válida en el menú de socios. Intente nuevamente.\n");
                    break;
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

    // Para el submenu Libros
    // Obtener codigo de libro a partir de isbn
    public static int obtenerCodLibroPorIsbn(String isbn) {
        String sql = "SELECT cod_libro FROM libros WHERE isbn = ?";
        try (Connection conn = connectMySQL()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, isbn);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("cod_libro");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }
        return -1;
    }

    // Alta tabla Libros
    public static void agregarLibro(Scanner scanner) {
        System.out.println("Has elegido: Altas.");

        String isbn = validarISBN(scanner, true);
        String titulo = validarNombre(scanner, "título del libro");
        int nCopias = validarNumeroCopias(scanner);
        int valoracion = validarValoracion(scanner);

        // Generar cod_libro aleatorio único
        int codLibro = validarCodigoGenerado(99999999, "libros", "cod_libro");

        try (Connection conn = connectMySQL()) {

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
        try (Connection conn = connectMySQL()) {
            for (int i = 0; i < nCopias; i++) {
                codEjemplar = validarCodigoGenerado(99999999, "ejemplares", "cod_ejemplar");

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
    public static void borrarLibro(Connection conn, String codLibroBaja) {
        int codLibro = obtenerCodLibroPorIsbn(codLibroBaja);
        if (codLibro == -1) {
            System.out.println("No se encontró el libro con ISBN: " + codLibroBaja);
        } else {

            int ejemplaresPrestados = comprobarEjemplaresPrestados(codLibro);

            if (ejemplaresPrestados == 0) {
                // No hay ejemplares prestados, borrar todos los registros
                borrarPrestamos(codLibro);
                borrarEjemplares(codLibro);
                borrarLibroPorIsbn(codLibroBaja);
                System.out.println("Se han borrado todos los registros del libro con ISBN: " + codLibroBaja);
            } else {
                // Hay ejemplares prestados, borrar solo los ejemplares no prestados
                borrarPrestamosDevueltos(codLibro);
                borrarEjemplaresDevueltos(codLibro);
                actualizarNumeroCopias(codLibro);
                System.out.println("Se han borrado algunos ejemplares del libro con ISBN: " + codLibroBaja
                        + ", pero hay ejemplares prestados.");
            }
        }
    }

    // Borrar prestamos
    public static void borrarPrestamos(int codLibro) {
        String deletePrestamosSql = "DELETE FROM prestamos WHERE cod_ejemplar IN (SELECT cod_ejemplar FROM ejemplares WHERE cod_libro = ?)";
        try (Connection conn = connectMySQL()) {
            try (PreparedStatement deletePrestamosStmt = conn.prepareStatement(deletePrestamosSql)) {
                deletePrestamosStmt.setInt(1, codLibro);
                deletePrestamosStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }
    }

    // Borrar prestamos que ya han sido devueltos de un mismo ejemplar, en el caso
    // que haya uno no devuelto, no los borra
    public static void borrarPrestamosDevueltos(int codLibro) {
        String deletePrestamosSql = "DELETE FROM prestamos \n" + //
                "WHERE cod_ejemplar IN (\n" + //
                "    SELECT cod_ejemplar FROM (\n" + //
                "        SELECT cod_ejemplar \n" + //
                "        FROM ejemplares \n" + //
                "        WHERE cod_libro = ? \n" + //
                "          AND cod_ejemplar NOT IN (\n" + //
                "              SELECT cod_ejemplar \n" + //
                "              FROM prestamos \n" + //
                "              WHERE fecha_devolucion IS NULL\n" + //
                "          )\n" + //
                "    ) AS temp\n" + //
                ")";
        try (Connection conn = connectMySQL()) {
            try (PreparedStatement deletePrestamosStmt = conn.prepareStatement(deletePrestamosSql)) {
                deletePrestamosStmt.setInt(1, codLibro);
                deletePrestamosStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }
    }

    // Borrar ejemplares
    public static void borrarEjemplares(int codLibro) {
        String deleteEjemplaresSql = "DELETE FROM ejemplares WHERE cod_libro = ?";
        try (Connection conn = connectMySQL()) {
            try (PreparedStatement deleteEjemplaresStmt = conn.prepareStatement(deleteEjemplaresSql)) {
                deleteEjemplaresStmt.setInt(1, codLibro);
                deleteEjemplaresStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }
    }

    // Borrar ejemplares devueltos
    public static void borrarEjemplaresDevueltos(int codLibro) {
        String deleteEjemplaresSql = "DELETE FROM ejemplares WHERE cod_libro = ? AND cod_ejemplar NOT IN (SELECT cod_ejemplar FROM prestamos WHERE fecha_devolucion IS NULL)";
        try (Connection conn = connectMySQL()) {
            try (PreparedStatement deleteEjemplaresStmt = conn.prepareStatement(deleteEjemplaresSql)) {
                deleteEjemplaresStmt.setInt(1, codLibro);
                deleteEjemplaresStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }
    }

    // Borrar libro por isbn
    public static void borrarLibroPorIsbn(String isbn) {
        String deleteLibrosSql = "DELETE FROM libros WHERE isbn = ?";
        try (Connection conn = connectMySQL()) {
            try (PreparedStatement deleteLibrosStmt = conn.prepareStatement(deleteLibrosSql)) {
                deleteLibrosStmt.setString(1, isbn);
                deleteLibrosStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }
    }

    // Actualizar numero de ejemplares de la tabla Libros
    public static void actualizarNumeroCopias(int codLibro) {
        String updateLibrosSql = "UPDATE libros SET n_copias = (SELECT COUNT(*) FROM ejemplares WHERE cod_libro = ?) WHERE cod_libro = ?";
        try (Connection conn = connectMySQL()) {
            try (PreparedStatement updateLibrosStmt = conn.prepareStatement(updateLibrosSql)) {
                updateLibrosStmt.setInt(1, codLibro);
                updateLibrosStmt.setInt(2, codLibro);
                updateLibrosStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }
    }

    // Modificar titulo tabla Libros
    public static void modificarTitulo(Connection conn, String isbnModificar, Scanner scanner) {
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
    public static void modificarNumeroCopias(Connection conn, String isbnModificar, Scanner scanner) {
        int nCopiasModificar = validarNumeroCopias(scanner);
        int nCopiasActuales = obtenerNumeroCopiasActuales(conn, isbnModificar);

        if (nCopiasActuales == -1) {
            System.out.println("Error al obtener el número actual de copias.");
            return;
        }

        if (nCopiasModificar > nCopiasActuales) {
            // Añadir ejemplares
            int copiasAAgregar = nCopiasModificar - nCopiasActuales;
            agregarEjemplares(obtenerCodLibroPorIsbn(isbnModificar), copiasAAgregar);
            nCopiasActuales = obtenerNumeroCopiasActuales(conn, isbnModificar);
            System.out.println(
                    "Se han añadido " + copiasAAgregar + " copias. Número de copias actual: " + nCopiasActuales);
        } else if (nCopiasModificar < nCopiasActuales) {
            // Eliminar ejemplares
            int copiasAEliminar = nCopiasActuales - nCopiasModificar;
            eliminarEjemplares(conn, obtenerCodLibroPorIsbn(isbnModificar), copiasAEliminar);
            nCopiasActuales = obtenerNumeroCopiasActuales(conn, isbnModificar);
            System.out.println(
                    "Se han eliminado " + copiasAEliminar + " copias. Número de copias actual: " + nCopiasActuales);
        } else {
            System.out.println("El número de copias es el mismo, no se realizaron cambios.");
        }

        // Actualizar el número de ejemplares en la tabla libros
        actualizarNumeroCopias(obtenerCodLibroPorIsbn(isbnModificar));
    }

    // Obtener numero de copias actuales para modificar numero de copias
    public static int obtenerNumeroCopiasActuales(Connection conn, String isbn) {
        String query = "SELECT COUNT(*) FROM ejemplares WHERE cod_libro = (SELECT cod_libro FROM libros WHERE isbn = ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el número de copias: " + e.getMessage());
        }
        return -1;
    }

    // Eliminar numero de ejemplares segun lo que se indique en el update
    public static void eliminarEjemplares(Connection conn, int codLibro, int cantidadAEliminar) {
        // Primero, obtenemos los ejemplares que se pueden eliminar
        String selectEjemplaresSql = "SELECT cod_ejemplar FROM ejemplares WHERE cod_libro = ? " +
                "AND cod_ejemplar NOT IN (SELECT cod_ejemplar FROM prestamos WHERE fecha_devolucion IS NULL) LIMIT ?";

        try (PreparedStatement selectEjemplaresStmt = conn.prepareStatement(selectEjemplaresSql)) {
            selectEjemplaresStmt.setInt(1, codLibro);
            selectEjemplaresStmt.setInt(2, cantidadAEliminar);
            ResultSet rs = selectEjemplaresStmt.executeQuery();

            List<Integer> ejemplaresAEliminar = new ArrayList<>();
            while (rs.next()) {
                ejemplaresAEliminar.add(rs.getInt("cod_ejemplar"));
            }

            // Si no hay ejemplares para eliminar, salimos
            if (ejemplaresAEliminar.isEmpty()) {
                System.out.println("No hay ejemplares disponibles para eliminar.");
                return;
            }

            // Borrar los préstamos asociados a los ejemplares que se van a eliminar
            String deletePrestamosSql = "DELETE FROM prestamos WHERE cod_ejemplar IN (";
            for (int i = 0; i < ejemplaresAEliminar.size(); i++) {
                deletePrestamosSql += "?";
                if (i < ejemplaresAEliminar.size() - 1) {
                    deletePrestamosSql += ",";
                }
            }
            deletePrestamosSql += ")";

            try (PreparedStatement deletePrestamosStmt = conn.prepareStatement(deletePrestamosSql)) {
                for (int i = 0; i < ejemplaresAEliminar.size(); i++) {
                    deletePrestamosStmt.setInt(i + 1, ejemplaresAEliminar.get(i));
                }
                deletePrestamosStmt.executeUpdate();
            }

            // Ahora, borrar los ejemplares
            String deleteEjemplaresSql = "DELETE FROM ejemplares WHERE cod_ejemplar IN (";
            for (int i = 0; i < ejemplaresAEliminar.size(); i++) {
                deleteEjemplaresSql += "?";
                if (i < ejemplaresAEliminar.size() - 1) {
                    deleteEjemplaresSql += ",";
                }
            }
            deleteEjemplaresSql += ")";

            try (PreparedStatement deleteEjemplaresStmt = conn.prepareStatement(deleteEjemplaresSql)) {
                for (int i = 0; i < ejemplaresAEliminar.size(); i++) {
                    deleteEjemplaresStmt.setInt(i + 1, ejemplaresAEliminar.get(i));
                }
                deleteEjemplaresStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar ejemplares: " + e.getMessage());
        }
    }

    // Modificar valoracion tabla Libros
    public static void modificarValoracion(Connection conn, String isbnModificar, Scanner scanner) {
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

    // Para el submenu Autores
    // Alta tabla Autores
    public static void agregarAutor(Scanner scanner) {
        System.out.println("Has elegido: Altas.");
        String nombre = validarNombre(scanner, "nombre del autor");
        String apellido = validarNombre(scanner, "apellido del autor");

        // Generar cod_autor aleatorio y comprobar que no exista
        int codAutor = validarCodigoGenerado(99999, "autores", "cod_autor");

        try (Connection conn = connectMySQL()) {

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
            pstmt.setString(1, codAutorBaja);
            borrados = pstmt.executeUpdate();
            return (borrados > 0);
        } catch (Exception e) {
            System.out.println("Problema al borrar: " + "\n" + e.getMessage());
            return false;
        }
    }

    // Modificar nombre/apellido tabla Autores
    public static void modificarNombre(Scanner scanner, Connection conn, String codAutorModificar, String tipo) {
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
    public static void modificarCodigoPais(Scanner scanner, Connection conn, String codAutorModificar) {
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

    // Para el submenu Usuarios
    // Alta tabla socios
    public static void agregarSocio(Scanner scanner) {

        String dni = validarDNI(scanner);
        String nombre = validarNombre(scanner, "nombre");
        String telefono = validarTelefono(scanner);
        String correo = validarCorreo(scanner);
        String usuario = validarNombre(scanner, "nombre de usuario");
        String contraseña = validarContraseña(scanner);

        int codSocio = validarCodigoGenerado(99999999, "usuarios", "cod_socio");
        int codUsuario = validarCodigoGenerado(99999999, "usuarios", "cod_usuario");

        try (Connection conn = connectMySQL()) {
            // Insertar socio
            String sql = "INSERT INTO usuarios (cod_usuario, cod_socio, dni, nombre, telefono, correo, usuario, contraseña) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, codUsuario);
                stmt.setInt(2, codSocio);
                stmt.setString(3, dni);
                stmt.setString(4, nombre);
                stmt.setString(5, telefono);
                stmt.setString(6, correo);
                stmt.setString(7, usuario);
                stmt.setString(8, contraseña);

                int filas = stmt.executeUpdate();
                if (filas > 0) {
                    System.out.println("Socio agregado correctamente con código de socio: " + codSocio);
                } else {
                    System.out.println("Error al insertar el socio.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }
    }

    // Bajas socio
    public static void borrarSocioPorDNI(Connection conn, String dni) {
        try {
            // Verificar si existe un socio con ese DNI
            String checkSql = "SELECT COUNT(*) FROM usuarios WHERE dni = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, dni);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    System.out.println("No se encontró ningún socio con ese DNI.");
                    return;
                }
            }

            // Eliminar socio
            String deleteSql = "DELETE FROM usuarios WHERE dni = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, dni);
                int filasAfectadas = deleteStmt.executeUpdate();
                if (filasAfectadas > 0) {
                    System.out.println("Socio eliminado correctamente.");
                } else {
                    System.out.println("No se pudo eliminar el socio.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al eliminar el socio:");
            e.printStackTrace();
        }
    }

    // Consultar socios
    public static void consultarSocios(Connection conn) {
        String sql = "SELECT cod_usuario, cod_socio, dni, nombre, telefono, correo, usuario FROM usuarios";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- Lista de Socios ---");

            boolean hayResultados = false;
            while (rs.next()) {
                hayResultados = true;
                System.out.println("Cod_Usuario: " + rs.getInt("cod_usuario"));
                System.out.println("Cod_Socio: " + rs.getInt("cod_socio"));
                System.out.println("DNI: " + rs.getString("dni"));
                System.out.println("Nombre: " + rs.getString("nombre"));
                System.out.println("Teléfono: " + rs.getString("telefono"));
                System.out.println("Correo: " + rs.getString("correo"));
                System.out.println("Usuario: " + rs.getString("usuario"));
                System.out.println("---------------------------");
            }

            if (!hayResultados) {
                System.out.println("No hay socios registrados.");
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar los socios:");
            e.printStackTrace();
        }
    }

    // Consultar socio por DNI
    public static boolean consultarSocioPorDNI(Connection conn, String dni) {
        String sql = "SELECT cod_usuario, cod_socio, dni, nombre, telefono, correo, usuario FROM usuarios WHERE dni = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dni);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n--- Datos del Socio ---");
                System.out.println("Cod_Usuario: " + rs.getInt("cod_usuario"));
                System.out.println("Cod_Socio: " + rs.getInt("cod_socio"));
                System.out.println("DNI: " + rs.getString("dni"));
                System.out.println("Nombre: " + rs.getString("nombre"));
                System.out.println("Teléfono: " + rs.getString("telefono"));
                System.out.println("Correo: " + rs.getString("correo"));
                System.out.println("Usuario: " + rs.getString("usuario"));
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar el socio:");
            e.printStackTrace();
            return false;
        }
    }

    // Para el submenu Prestamos
    // Realizar préstamo de un libro
    public static void realizarPrestamo(int codLibro, int codUsuario) {
        int codEjemplar = 0;

        try (Connection conn = connectMySQL()) {
            // Verificar si hay un ejemplar disponible
            String queryEjemplar = "SELECT e.cod_ejemplar FROM ejemplares e " +
                    "LEFT JOIN prestamos p ON e.cod_ejemplar = p.cod_ejemplar " +
                    "WHERE e.cod_libro = ? AND (p.fecha_devolucion IS NOT NULL OR p.cod_ejemplar IS NULL) " +
                    "LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(queryEjemplar)) {
                stmt.setInt(1, codLibro);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    codEjemplar = rs.getInt("cod_ejemplar");
                } else {
                    System.out.println("No hay ejemplares disponibles para este libro.");
                    return; // Salir del método si no hay ejemplares disponibles
                }
            }

            // Verificar si el usuario ya tiene un préstamo activo para el mismo libro
            String queryPrestamo = "SELECT COUNT(*) FROM prestamos p JOIN ejemplares e ON p.cod_ejemplar = e.cod_ejemplar "
                    + "WHERE p.cod_usuario = ? AND e.cod_libro = ? AND p.fecha_devolucion IS NULL";
            try (PreparedStatement stmt = conn.prepareStatement(queryPrestamo)) {
                stmt.setInt(1, codUsuario);
                stmt.setInt(2, codLibro);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("El usuario ya tiene un préstamo activo para este libro.");
                    return; // Salir del método si ya tiene un préstamo activo
                }
            }

            // Realizar el préstamo
            String insertPrestamo = "INSERT INTO prestamos (cod_prestamo, cod_ejemplar, cod_usuario, fecha_prestamo) VALUES (?, ?, ?, CURRENT_DATE)";
            int codPrestamo = validarCodigoGenerado(9999999, "prestamos", "cod_prestamo");
            try (PreparedStatement stmt = conn.prepareStatement(insertPrestamo)) {
                stmt.setInt(1, codPrestamo);
                stmt.setInt(2, codEjemplar);
                stmt.setInt(3, codUsuario);
                stmt.executeUpdate();
                System.out.println("Préstamo realizado con éxito.");
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }

    }

    // Devolver libro
    public static void realizarDevolucion(Connection conn, int codUsuario) {
        String sqlDevolver = "UPDATE prestamos SET fecha_devolucion = CURRENT_DATE WHERE cod_usuario = ? AND fecha_devolucion IS NULL";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDevolver)) {
            pstmt.setInt(1, codUsuario);
            int filasActualizadas = pstmt.executeUpdate();
            if (filasActualizadas > 0) {
                System.out.println("Libros devueltos: " + filasActualizadas);
            } else {
                System.out.println("No hay libros que devolver");
            }
        } catch (SQLException e) {
            System.out.println("Error al devolver el libro: " + e.getMessage());
        }
    }

    public static void realizarDevolucion(Connection conn, int codUsuario, int codLibro) {
        String sqlDevolver = "UPDATE prestamos SET fecha_devolucion = CURRENT_DATE " +
                "WHERE cod_ejemplar IN (SELECT cod_ejemplar FROM ejemplares WHERE cod_libro = ?) " +
                "AND cod_usuario = ? AND fecha_devolucion IS NULL";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDevolver)) {
            pstmt.setInt(1, codLibro);
            pstmt.setInt(2, codUsuario);
            int filasActualizadas = pstmt.executeUpdate();
            if (filasActualizadas > 0) {
                System.out.println("Libros devueltos con ese isbn: " + filasActualizadas);
            } else {
                System.out.println("No hay libros que devolver con ese isbn");
            }
        } catch (SQLException e) {
            System.out.println("Error al devolver el libro: " + e.getMessage());
        }
    }

    // Consultar disponibilidad de libros
    public static void consultarDisponibilidadLibros(Connection conn) {
        String sqlTotalEjemplares = "SELECT l.titulo, l.isbn, COUNT(e.cod_ejemplar) AS total_ejemplares " +
                "FROM libros l " +
                "JOIN ejemplares e ON l.cod_libro = e.cod_libro " +
                "GROUP BY l.cod_libro";

        String sqlEjemplaresDisponibles = "SELECT l.titulo, COUNT(e.cod_ejemplar) AS ejemplares_no_disponibles " +
                "FROM libros l " +
                "JOIN ejemplares e ON l.cod_libro = e.cod_libro " +
                "JOIN prestamos p ON e.cod_ejemplar = p.cod_ejemplar " +
                "WHERE p.fecha_devolucion IS NULL ";

        try (PreparedStatement pstmtTotal = conn.prepareStatement(sqlTotalEjemplares);
                ResultSet rsTotal = pstmtTotal.executeQuery()) {

            System.out.println("Libros disponibles:");
            boolean hayResultados = false;

            while (rsTotal.next()) {
                hayResultados = true;
                String titulo = rsTotal.getString("titulo");
                long isbn = rsTotal.getLong("isbn");
                int totalEjemplares = rsTotal.getInt("total_ejemplares");

                // Ejecutar la consulta de ejemplares disponibles para cada libro
                try (PreparedStatement pstmtDisponiblesPorLibro = conn
                        .prepareStatement(sqlEjemplaresDisponibles + " AND l.titulo = ?")) {
                    pstmtDisponiblesPorLibro.setString(1, titulo);
                    ResultSet rsDisponibles = pstmtDisponiblesPorLibro.executeQuery();

                    int noDisponibles = 0;
                    if (rsDisponibles.next()) {
                        noDisponibles = rsDisponibles.getInt("ejemplares_no_disponibles");
                    }

                    totalEjemplares -= noDisponibles;
                    if (totalEjemplares > 0) {
                        System.out
                                .println("Título: " + titulo + ", ISBN: " + isbn + ", Disponibles: " + totalEjemplares);
                    }
                }
            }

            if (!hayResultados) {
                System.out.println("No se encontraron libros disponibles.");
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar disponibilidad de libros: " + e.getMessage());
        }
    }

    // Consultar disponibilidad de libros con filtro
    public static void consultarDisponibilidadLibrosFiltrado(Connection conn, String tipo, String valor) {
        String sqlTotalEjemplares = "SELECT l.titulo, l.isbn, COUNT(e.cod_ejemplar) AS total_ejemplares " +
                "FROM libros l " +
                "JOIN ejemplares e ON l.cod_libro = e.cod_libro " +
                "WHERE l." + tipo + " = ? " +
                "GROUP BY l.cod_libro";

        String sqlEjemplaresNoDisponibles = "SELECT COUNT(e.cod_ejemplar) AS ejemplares_no_disponibles " +
                "FROM ejemplares e " +
                "JOIN prestamos p ON e.cod_ejemplar = p.cod_ejemplar " +
                "JOIN libros l ON e.cod_libro = l.cod_libro " +
                "WHERE p.fecha_devolucion IS NULL AND l." + tipo + " = ?";

        try (PreparedStatement pstmtTotal = conn.prepareStatement(sqlTotalEjemplares)) {
            pstmtTotal.setString(1, valor);
            ResultSet rsTotal = pstmtTotal.executeQuery();

            System.out.println("Libros disponibles:");
            boolean hayResultados = false;

            while (rsTotal.next()) {
                hayResultados = true;
                String titulo = rsTotal.getString("titulo");
                long isbn = rsTotal.getLong("isbn");
                int totalEjemplares = rsTotal.getInt("total_ejemplares");

                // Ejecutar la consulta de ejemplares no disponibles para cada libro
                try (PreparedStatement pstmtNoDisponibles = conn.prepareStatement(sqlEjemplaresNoDisponibles)) {
                    pstmtNoDisponibles.setString(1, valor); // Usar el valor del tipo
                    ResultSet rsNoDisponibles = pstmtNoDisponibles.executeQuery();

                    int noDisponibles = 0;
                    if (rsNoDisponibles.next()) {
                        noDisponibles = rsNoDisponibles.getInt("ejemplares_no_disponibles");
                    }

                    int disponibles = totalEjemplares - noDisponibles;
                    if (disponibles > 0) {
                        System.out.println("Título: " + titulo + ", ISBN: " + isbn + ", Disponibles: " + disponibles);
                    } else {
                        hayResultados = false;
                    }
                }
            }

            if (!hayResultados) {
                System.out.println("No se encontraron libros disponibles con el " + tipo + " proporcionado.");
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar disponibilidad de libros: " + e.getMessage());
        }
    }

    // Consultar prestamos de un usuario
    public static void consultarPrestamosUsuario(Connection conn, int codUsuario) {
        String sql = "SELECT l.titulo, l.isbn, " +
                "DATEDIFF(CURRENT_DATE, p.fecha_prestamo) AS dias_prestado " +
                "FROM libros l " +
                "JOIN ejemplares e ON l.cod_libro = e.cod_libro " +
                "JOIN prestamos p ON e.cod_ejemplar = p.cod_ejemplar " +
                "WHERE p.cod_usuario = ? AND p.fecha_devolucion IS NULL"; // Solo libros que no han sido devueltos

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, codUsuario); // Establecer el cod_usuario en la consulta
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Libros prestados al usuario con código " + codUsuario + ":");
            boolean hayResultados = false;
            while (rs.next()) {
                hayResultados = true;
                String titulo = rs.getString("titulo");
                long isbn = rs.getLong("isbn");
                int diasConLibro = rs.getInt("dias_prestado");
                int diasRetraso = diasConLibro - 7;
                System.out.println("Título: " + titulo + ", ISBN: " + isbn +
                        ", Días con el libro: " + diasConLibro +
                        ", Días de retraso: " + (diasRetraso > 0 ? diasRetraso : 0));
            }
            if (!hayResultados) {
                System.out.println("No se encontraron libros prestados.");
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar libros prestados: " + e.getMessage());
        }
    }

    /// Validadores ///
    // Validar codigo generado unico
    private static int validarCodigoGenerado(int cantidad, String tabla, String campo) {
        int codLibro = 0;
        boolean codUnico = false;
        try (Connection conn = connectMySQL()) {
            do {
                codLibro = generarCodigo(cantidad);

                String checkSql = "SELECT COUNT(*) FROM " + tabla + " WHERE " + campo + " = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, codLibro);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) == 0) {
                        codUnico = true;
                    }
                }
            } while (!codUnico);
        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }
        return codLibro;
    }

    // Validar ISBN: exactamente 13 dígitos numéricos
    private static String validarISBN(Scanner scanner, boolean verificarExistencia) {
        String isbn;
        do {
            System.out.print("Introduce el ISBN del libro (13 dígitos): ");
            isbn = scanner.nextLine().trim();
            if (isbn.isEmpty() || !isbn.matches("\\d{13}")) {
                System.out.println("El ISBN debe contener exactamente 13 dígitos numéricos.");
                continue; // Volver a preguntar si el formato es incorrecto
            }
            if (verificarExistencia && isbnYaExiste(isbn)) {
                System.out.println("El ISBN ya existe en la base de datos. Por favor, introduce otro ISBN.");
            }
        } while (isbn.isEmpty() || !isbn.matches("\\d{13}") || (verificarExistencia && isbnYaExiste(isbn)));
        return isbn;
    }

    // Método para verificar si el ISBN ya existe en la base de datos
    private static boolean isbnYaExiste(String isbn) {
        String query = "SELECT COUNT(*) FROM libros WHERE isbn = ?";
        try (Connection conn = connectMySQL()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, isbn);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }
        return false;
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

    // Validar inicio de sesión de usuario
    private static int validarInicioSesion(Scanner scanner) {
        String nom = "";
        String pass = "";
        int codUsuario = -1;

        // Pedir nombre de usuario y contraseña
        System.out.println("Introduzca nombre de usuario: ");
        nom = scanner.nextLine().trim();
        if (nom.isEmpty()) {
            System.out.println("Nombre de usuario no puede estar vacíos. Saliendo...");
            return -1;
        }
        System.out.println("Introduzca contraseña: ");
        pass = scanner.nextLine().trim();
        if (pass.isEmpty()) {
            System.out.println("Contraseña no puede estar vacíos. Saliendo...");
            return -1;
        }

        // Conectar a la base de datos
        try (Connection conn = connectMySQL()) {
            // verificar si el usuario existe
            String checkInicioSesion = "SELECT cod_usuario FROM usuarios WHERE usuario = ? AND contraseña = ?";
            try (PreparedStatement st = conn.prepareStatement(checkInicioSesion)) {
                st.setString(1, nom);
                st.setString(2, pass);
                ResultSet rs = st.executeQuery();

                if (rs.next()) {
                    codUsuario = rs.getInt("cod_usuario"); // Usuario encontrado
                }
            }
        } catch (Exception e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }

        return codUsuario;
    }

    // Comprobar penalización del usuario
    private static int comprobarPenalizacion(int codUsuario) {
        Date fechaPrestamo;
        Date fechaDevolucion;
        int maxPenalizacion = 0;

        // Conectar a la base de datos
        try (Connection conn = connectMySQL()) {
            // Obtener la fecha de préstamo y la fecha de devolución
            String query = "SELECT fecha_prestamo, fecha_devolucion FROM prestamos WHERE cod_usuario = ?;";
            try (PreparedStatement st = conn.prepareStatement(query)) {
                st.setInt(1, codUsuario);
                ResultSet rs = st.executeQuery();

                while (rs.next()) {
                    fechaPrestamo = rs.getDate("fecha_prestamo");
                    fechaDevolucion = rs.getDate("fecha_devolucion");

                    int diasRetraso = 0;

                    if (fechaDevolucion == null) {
                        // Calcular los días de retraso si no ha sido devuelto
                        diasRetraso = (int) ((new Date(System.currentTimeMillis()).getTime() - fechaPrestamo.getTime())
                                / (1000 * 60 * 60 * 24)) - 7;
                        if (diasRetraso < 0) {
                            diasRetraso = 0; // No hay penalización si no hay días de retraso
                        }
                    } else {
                        // Calcular días de tardanza si ya ha sido devuelto
                        int diasTardanza = (int) ((fechaDevolucion.getTime() - fechaPrestamo.getTime())
                                / (1000 * 60 * 60 * 24)) - 7;
                        int diasDesdeDevolucion = (int) ((new Date(System.currentTimeMillis()).getTime()
                                - fechaDevolucion.getTime()) / (1000 * 60 * 60 * 24));

                        // Solo considerar días de tardanza si son positivos
                        if (diasTardanza > 0 && diasTardanza < diasDesdeDevolucion) {
                            diasRetraso = diasTardanza - diasDesdeDevolucion;
                        } else {
                            diasRetraso = 0;
                        }
                    }

                    // Calcular la penalización para este préstamo
                    int penalizacion = calcularPenalizacionPorRetraso(diasRetraso);

                    // Retener la penalización más alta
                    if (penalizacion > maxPenalizacion) {
                        maxPenalizacion = penalizacion;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }

        // Solo actualizar la penalización si hay días de penalización
        if (maxPenalizacion > 0) {
            actualizarPenalizacion(codUsuario, maxPenalizacion);
        } else {
            // Si no hay penalización, asegurarse de que se establezca a 0
            actualizarPenalizacion(codUsuario, 0);
        }

        return maxPenalizacion;
    }

    // Método para calcular la penalización según los días de retraso
    private static int calcularPenalizacionPorRetraso(long diasRetraso) {
        if (diasRetraso < 0) {
            return 0;
        } else if (diasRetraso < 7) {
            return 1;
        } else if (diasRetraso < 15) {
            return 2;
        } else {
            return 3;
        }
    }

    private static void actualizarPenalizacion(int codUsuario, int nuevaPenalizacion) {
        // Asegúrate de que nuevaPenalizacion esté entre 0 y 3
        if (nuevaPenalizacion < 0 || nuevaPenalizacion > 3) {
            System.out.println("Error: La penalización debe estar entre 0 y 3.");
            return; // O maneja el error de otra manera
        }

        try (Connection conn = connectMySQL()) {
            String updateQuery = "UPDATE usuarios SET cod_penalizacion = ? WHERE cod_usuario = ?";
            try (PreparedStatement st = conn.prepareStatement(updateQuery)) {
                st.setInt(1, nuevaPenalizacion);
                st.setInt(2, codUsuario);
                st.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }
    }

    private static int diasPenalizacionRestantes(int codUsuario) {
        Date fechaPrestamo;
        Date fechaDevolucion;
        int maxDiasRetraso = 0;

        // Conectar a la base de datos
        try (Connection conn = connectMySQL()) {
            // Obtener la fecha de préstamo y la fecha de devolución
            String query = "SELECT fecha_prestamo, fecha_devolucion FROM prestamos WHERE cod_usuario = ?;";
            try (PreparedStatement st = conn.prepareStatement(query)) {
                st.setInt(1, codUsuario);
                ResultSet rs = st.executeQuery();

                while (rs.next()) {
                    fechaPrestamo = rs.getDate("fecha_prestamo");
                    fechaDevolucion = rs.getDate("fecha_devolucion");

                    int diasRetraso = 0;

                    if (fechaDevolucion == null) {
                        // Calcular los días de retraso si no ha sido devuelto
                        diasRetraso = (int) ((new Date(System.currentTimeMillis()).getTime() - fechaPrestamo.getTime())
                                / (1000 * 60 * 60 * 24)) - 7;
                        if (diasRetraso < 0) {
                            diasRetraso = 0; // No hay penalización si no hay días de retraso
                        }
                    } else {
                        // Calcular días de tardanza si ya ha sido devuelto
                        int diasTardanza = (int) ((fechaDevolucion.getTime() - fechaPrestamo.getTime())
                                / (1000 * 60 * 60 * 24)) - 7;
                        int diasDesdeDevolucion = (int) ((new Date(System.currentTimeMillis()).getTime()
                                - fechaDevolucion.getTime()) / (1000 * 60 * 60 * 24));

                        // Solo considerar días de tardanza si son positivos
                        if (diasTardanza > 0 && diasTardanza > diasDesdeDevolucion) {
                            diasRetraso = diasTardanza - diasDesdeDevolucion;
                        } else {
                            diasRetraso = 0;
                        }
                    }

                    // Retener la penalización más alta
                    if (diasRetraso > maxDiasRetraso) {
                        maxDiasRetraso = diasRetraso;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }

        return maxDiasRetraso;
    }

    // Comprobar ejemplares que estan prestados
    private static int comprobarEjemplaresPrestados(int codLibro) {
        String checkPrestamosSql = "SELECT COUNT(*) FROM prestamos p JOIN ejemplares e ON p.cod_ejemplar = e.cod_ejemplar WHERE e.cod_libro = ? AND p.fecha_devolucion IS NULL";
        try (Connection conn = connectMySQL()) {
            try (PreparedStatement checkPrestamosStmt = conn.prepareStatement(checkPrestamosSql)) {
                checkPrestamosStmt.setInt(1, codLibro);
                ResultSet rs = checkPrestamosStmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }
        return 0;
    }

    // Validar DNI: que tenga 8 digitos numericos y una letra al final
    private static String validarDNI(Scanner scanner) {
        String dni;
        do {
            System.out.print("Introduce el DNI (formato 8 números y una letra): ");
            dni = scanner.nextLine().trim().toUpperCase();
        } while (!dni.matches("\\d{8}[A-Z]"));
        return dni;
    }

    // Validar telefono: que tenga 9 digitos numericos
    private static String validarTelefono(Scanner scanner) {
        String telefono;
        do {
            System.out.print("Introduce el teléfono (9 dígitos): ");
            telefono = scanner.nextLine().trim();
        } while (!telefono.matches("\\d{9}"));
        return telefono;
    }

    // Validar correo: que tenga estructura de correo electronico valido
    private static String validarCorreo(Scanner scanner) {
        String correo;
        do {
            System.out.print("Introduce el correo electrónico: ");
            correo = scanner.nextLine().trim();
        } while (!correo.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$"));
        return correo;
    }

    // Validar contraseña: que tenga minimo 4 caracteres
    private static String validarContraseña(Scanner scanner) {
        String pass;
        do {
            System.out.print("Introduce una contraseña (mínimo 4 caracteres): ");
            pass = scanner.nextLine().trim();
        } while (pass.length() < 4);
        return pass;
    }

    // Validar permitir realizar prestamo
    private static boolean validarRealizarPrestamo(int codUsuario) {
        int librosPermitidos = 0;
        int librosPrestados = 0;

        try (Connection conn = connectMySQL()) {
            // Verificar la penalización del usuario y cuántos libros puede tener prestados
            String queryPenalizacion = "SELECT p.cod_penalizacion, p.libros_permitidos " +
                    "FROM usuarios u " +
                    "JOIN penalizaciones p ON u.cod_penalizacion = p.cod_penalizacion " +
                    "WHERE u.cod_usuario = ?";
            try (PreparedStatement stmt = conn.prepareStatement(queryPenalizacion)) {
                stmt.setInt(1, codUsuario);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    librosPermitidos = rs.getInt("libros_permitidos");
                } else {
                    return false;
                }
            }

            // Verificar cuántos libros tiene prestados el usuario
            String queryLibrosPrestados = "SELECT COUNT(*) FROM prestamos WHERE cod_usuario = ? AND fecha_devolucion IS NULL";
            try (PreparedStatement stmt = conn.prepareStatement(queryLibrosPrestados)) {
                stmt.setInt(1, codUsuario);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    librosPrestados = rs.getInt(1);
                }
            }

            // Validar si el usuario ha alcanzado el límite de libros permitidos
            if (librosPrestados >= librosPermitidos) {
                return false;
            } else {
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error de conexión o SQL:");
            e.printStackTrace();
        }
        return false;
    }
}
