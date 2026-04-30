package api.proyecto.redes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para testear la conexión a la base de datos
 */
@RestController
@RequestMapping("/api/test")
public class ConnectionTestController {

    @Autowired
    private DataSource dataSource;

    /**
     * Endpoint para testear la conexión a MySQL
     * URL: GET http://localhost:8080/api/test/connection
     */
    @GetMapping("/connection")
    public ResponseEntity<?> testConnection() {
        Map<String, Object> response = new HashMap<>();

        try {
            Connection connection = dataSource.getConnection();

            if (connection != null && !connection.isClosed()) {
                response.put("status", "success");
                response.put("message", "¡Conexión exitosa a la base de datos!");
                response.put("details", Map.of(
                    "database", connection.getCatalog(),
                    "url", connection.getMetaData().getURL(),
                    "user", connection.getMetaData().getUserName(),
                    "driver", connection.getMetaData().getDriverName(),
                    "driverVersion", connection.getMetaData().getDriverVersion(),
                    "databaseProduct", connection.getMetaData().getDatabaseProductName(),
                    "databaseVersion", connection.getMetaData().getDatabaseProductVersion()
                ));

                connection.close();
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Error: No se pudo establecer la conexión");
                return ResponseEntity.status(500).body(response);
            }

        } catch (SQLException e) {
            response.put("status", "error");
            response.put("message", "Error SQL al conectar con la base de datos");
            response.put("details", Map.of(
                "errorMessage", e.getMessage(),
                "sqlState", e.getSQLState(),
                "errorCode", e.getErrorCode()
            ));
            return ResponseEntity.status(500).body(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error inesperado");
            response.put("details", Map.of("error", e.getMessage()));
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Endpoint de prueba básica
     * URL: GET http://localhost:8080/api/test/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Aplicación en funcionamiento");
        return ResponseEntity.ok(response);
    }
}
