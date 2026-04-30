package api.proyecto.redes.controller;

import api.proyecto.redes.dto.ConductorRequest;
import api.proyecto.redes.dto.ConductorResponse;
import api.proyecto.redes.model.Conductor;
import api.proyecto.redes.service.ConductorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/conductores")
public class ConductorController {

    private final ConductorService conductorService;

    public ConductorController(ConductorService conductorService) {
        this.conductorService = conductorService;
    }

    @GetMapping
    public List<ConductorResponse> listar(@RequestParam(value = "disponible", required = false) Boolean disponible) {
        return conductorService.listar(disponible).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ConductorResponse obtener(@PathVariable Long id) {
        return toResponse(conductorService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ConductorResponse> crear(@RequestBody ConductorRequest request) {
        validarCrear(request);
        Conductor conductor = new Conductor();
        conductor.setLicencia(request.licencia());
        conductor.setVehiculo(request.vehiculo());
        conductor.setCalificacionPromedio(request.calificacionPromedio());
        conductor.setDisponible(request.disponible());

        Conductor creado = conductorService.crear(conductor, request.usuarioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(creado));
    }

    @PutMapping("/{id}")
    public ConductorResponse actualizar(@PathVariable Long id, @RequestBody ConductorRequest request) {
        Conductor datos = new Conductor();
        datos.setLicencia(request.licencia());
        datos.setVehiculo(request.vehiculo());
        datos.setCalificacionPromedio(request.calificacionPromedio());
        datos.setDisponible(request.disponible());

        return toResponse(conductorService.actualizar(id, datos, request.usuarioId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        conductorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private void validarCrear(ConductorRequest request) {
        if (request == null || request.usuarioId() == null || request.usuarioId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "usuarioId es requerido");
        }
        if (request.licencia() == null || request.licencia().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "licencia es requerida");
        }
    }

    private ConductorResponse toResponse(Conductor conductor) {
        Long usuarioId = conductor.getUsuario() != null ? conductor.getUsuario().getIdUsuario() : null;
        return new ConductorResponse(
            conductor.getIdConductor(),
            usuarioId,
            conductor.getLicencia(),
            conductor.getVehiculo(),
            conductor.getCalificacionPromedio(),
            conductor.getDisponible(),
            conductor.getCreadoEn()
        );
    }
}
