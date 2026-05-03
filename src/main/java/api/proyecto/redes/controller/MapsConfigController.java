package api.proyecto.redes.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class MapsConfigController {

    @Value("${app.maps.api-key:}")
    private String mapsApiKey;

    @GetMapping("/maps-key")
    public ResponseEntity<Map<String, String>> getMapsKey() {
        if (mapsApiKey == null || mapsApiKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "API key no configurada"));
        }
        return ResponseEntity.ok(Map.of("key", mapsApiKey));
    }
}
