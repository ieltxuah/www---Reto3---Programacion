import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GestionBibliotecaMuskiz {
    public static void main(String[] args) throws Exception {
        Connection conn = connectMySQL();
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Conexión a la base de datos cerrada.");
            } catch (SQLException e) {
                System.out.println(
                        "Error al cerrar la conexión a la base de datos, por favor crea un ticket con el error para solventar el problema: "
                                + e.getMessage());
            }
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
}
