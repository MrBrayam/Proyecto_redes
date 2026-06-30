package api.proyecto.redes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI aristaRideOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("AristaRideAI API")
                .description("Documentacion de endpoints REST para pasajeros, conductores, admin, viajes, pagos y reportes")
                .version("v1")
                .contact(new Contact()
                    .name("AristaRideAI Team")
                    .email("support@aristaride.local"))
                .license(new License()
                    .name("Uso academico")
                    .url("https://example.com/license")));
    }
}
