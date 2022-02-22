package mx.com.ga.cosmonaut.nomina.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.nomina.service.PercepcionService;

import javax.inject.Inject;

@Controller("/percepciones")
public class PercepcionController {

    @Inject
    private PercepcionService percepcionService;

    @Operation(summary = "${cosmonaut.controller.percepciones.listarempleado.resumen}",
            description = "${cosmonaut.controller.percepciones.listarempleado.descripcion}",
            operationId = "percepciones.listarempleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Percepciones - Listar Empleado")
    @Get(value = "/listar/empleado/{nominaId}/{personaId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listarEmpleado(@PathVariable Integer nominaId, @PathVariable Integer personaId){
        try {
            return HttpResponse.ok(percepcionService.listarEmpleado(nominaId,personaId));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
