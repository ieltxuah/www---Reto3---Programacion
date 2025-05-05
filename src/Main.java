import java.util.Scanner; // Importación de la clase Scanner

public class Main {
    public static void main(String[] args) throws Exception {
        // Crear objeto scanner
        Scanner scanner = new Scanner(System.in);
        String opcion = ""; // Variable para la opción del menú principal

        // Encabezado
        System.out.println("=========================================================");
        System.out.println("=============== Gestión Biblioteca Muskiz ===============");
        System.out.println("=========================================================");
        System.out.println("Bienvenido al programa Gestor de la Biblioteca de Muskiz");

        // Bucle menú principal
        while (true) {
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
                    return; // Salimos del programa

                default:
                    System.out.println("Opción no válida. Por favor, intente de nuevo.\n");
                    continue; // Volver a preguntar
            }
            break;
        }
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
