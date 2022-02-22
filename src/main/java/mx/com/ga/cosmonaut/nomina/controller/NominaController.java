package mx.com.ga.cosmonaut.nomina.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.orquestador.service.NominaService;

import javax.inject.Inject;

@Controller("/nomina")
public class NominaController {

    @Inject
    private NominaService nominaService;

    @Operation(summary = "${cosmonaut.controller.calculo.consultaestatus.resumen}",
            description = "${cosmonaut.controller.calculo.consultaestatus.descripcion}",
            operationId = "calculo.consultaestatus")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nomina - Consulta estatus")
    @Get(value = "/consulta/estatus/{centroClienteId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> consultaEstatus(@PathVariable Integer centroClienteId){
        try {
            return HttpResponse.ok(nominaService.obtenEstatusProceso(centroClienteId));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.calculo.consultaestatus.resumen}",
            description = "${cosmonaut.controller.calculo.consultaestatus.descripcion}",
            operationId = "calculo.consultaestatus")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nomina - Consulta estatus")
    @Post(value = "/actualiza/visto/{nominaXperiodoId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> actualizaVista(@PathVariable Integer nominaXperiodoId){
        try {
            return HttpResponse.ok(nominaService.actualizaVista(nominaXperiodoId));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
