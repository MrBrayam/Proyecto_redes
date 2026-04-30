package api.proyecto.redes.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Componente para testear la conexión a la base de datos MySQL
 */
@Component
public class DatabaseConnectionTest implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n========================================");
        System.out.println("Iniciando Test de Conexión a MySQL");
        System.out.println("========================================\n");

        try {
            // Obtener conexión del DataSource
            Connection connection = dataSource.getConnection();

            if (connection != null && !connection.isClosed()) {
                System.out.println("Conexión exitosa a la base de datos");
                System.out.println("  - Base de Datos: " + connection.getCatalog());
                System.out.println("  - URL: " + connection.getMetaData().getURL());
                System.out.println("  - Usuario: " + connection.getMetaData().getUserName());
                System.out.println("  - Driver: " + connection.getMetaData().getDriverName());
                System.out.println("  - Versión del Driver: " + connection.getMetaData().getDriverVersion());
                System.out.println("  - Producto BD: " + connection.getMetaData().getDatabaseProductName());
                System.out.println("  - Versión BD: " + connection.getMetaData().getDatabaseProductVersion());

                connection.close();
                System.out.println("\nConexión cerrada correctamente");
            } else {
                System.out.println("Error: No se pudo establecer la conexión");
            }

        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos:");
            System.out.println("  Mensaje: " + e.getMessage());
            System.out.println("  SQL State: " + e.getSQLState());
            System.out.println("  Error Code: " + e.getErrorCode());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error inesperado:");
            e.printStackTrace();
        }

        System.out.println("\n========================================\n");
    }
}
