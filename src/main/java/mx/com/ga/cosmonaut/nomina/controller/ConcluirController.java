package mx.com.ga.cosmonaut.nomina.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.nomina.service.ConcluirService;

import javax.inject.Inject;

@Controller("/concluir")
public class ConcluirController {

    @Inject
    private ConcluirService concluirService;

    @Operation(summary = "${cosmonaut.controller.obtenerConcluir.resumen}",
            description = "${cosmonaut.controller.obtenerConcluir.descripcion}",
            operationId = "obtenerConcluir")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Concluir - Obtener concluir")
    @Get(value = "/{nominaPeriodoId}/{centroClienteId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerConcluir(@PathVariable Integer nominaPeriodoId, @PathVariable Integer centroClienteId){
        try {
            return HttpResponse.ok(concluirService.obtenerConcluir(nominaPeriodoId, centroClienteId));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.concluirNomina.resumen}",
            description = "${cosmonaut.controller.concluirNomina.descripcion}",
            operationId = "concluirNomina")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Concluir - Concluir")
    @Post(value = "/{nominaPeriodoId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> concluirNomina(@Header("datos-flujo") String datosFlujo,
                                                          @Header("datos-sesion") String datosSesion,
                                                          @PathVariable Integer nominaPeriodoId){
        try {
            return HttpResponse.ok(concluirService.concluirNomina(nominaPeriodoId));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
