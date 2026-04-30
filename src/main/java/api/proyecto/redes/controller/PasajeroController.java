package api.proyecto.redes.controller;

import api.proyecto.redes.dto.PasajeroRequest;
import api.proyecto.redes.dto.PasajeroResponse;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.service.PasajeroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/pasajeros")
public class PasajeroController {

    private final PasajeroService pasajeroService;

    public PasajeroController(PasajeroService pasajeroService) {
        this.pasajeroService = pasajeroService;
    }

    @GetMapping
    public List<PasajeroResponse> listar() {
        return pasajeroService.listar().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public PasajeroResponse obtener(@PathVariable Long id) {
        return toResponse(pasajeroService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<PasajeroResponse> crear(@RequestBody PasajeroRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setPassword(request.password());

        Usuario creado = pasajeroService.crear(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(creado));
    }

    @PutMapping("/{id}")
    public PasajeroResponse actualizar(@PathVariable Long id, @RequestBody PasajeroRequest request) {
        Usuario datos = new Usuario();
        datos.setNombre(request.nombre());
        datos.setEmail(request.email());
        datos.setPassword(request.password());
        return toResponse(pasajeroService.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pasajeroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private PasajeroResponse toResponse(Usuario usuario) {
        return new PasajeroResponse(
            usuario.getIdUsuario(),
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getCreadoEn()
        );
    }
}
