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
import mx.com.ga.cosmonaut.nomina.dto.DispersionDto;
import mx.com.ga.cosmonaut.nomina.service.TimbradoService;

import javax.inject.Inject;
import java.util.List;

@Controller("/timbrado")
public class TimbradoController {
    
    @Inject
    private TimbradoService timbradoService;

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.timbrado.resumen}",
            description = "${cosmonaut.controller.timbrado.descripcion}",
            operationId = "timbrado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Timbrado - Timbrado")
    @Put(value = "/{empresaId}/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> timbrado(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @Body List<DispersionDto> listaDispersionDto,
                                                    @PathVariable Integer empresaId){
        try {
            return HttpResponse.ok(timbradoService.timbrado(empresaId, listaDispersionDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.timbrado.respuesta.resumen}",
            description = "${cosmonaut.controller.timbrado.respuesta.descripcion}",
            operationId = "timbrado.respuesta")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Timbrado - Respuesta Dispersion")
    @Post(value = "/respuesta/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> respuesta(@Body String respuesta){
        try {
            return HttpResponse.ok(timbradoService.respuesta(respuesta));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.timbrado.procesando.resumen}",
            description = "${cosmonaut.controller.timbrado.procesando.descripcion}",
            operationId = "timbrado.procesando")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Timbrado - Procesando Timbrado")
    @Post(value = "/procesando/{nominaPeriodoId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> procesando(@PathVariable Long nominaPeriodoId,@Body List<Integer> lista){
        try {
            return HttpResponse.ok(timbradoService.procesando(nominaPeriodoId, lista));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.timbrado.consultadisponibles.resumen}",
            description = "${cosmonaut.controller.timbrado.consultadisponibles.descripcion}",
            operationId = "timbrado.consultadisponibles")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Timbrado - Consulta Timbres Disponibles")
    @Get(value = "/consulta/disponibles/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> consultaDisponibles(){
        try {
            return HttpResponse.ok(timbradoService.consultaDisponibles());
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.timbrado.resumen.resumen}",
            description = "${cosmonaut.controller.timbrado.resumen.descripcion}",
            operationId = "timbrado.resumen")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Timbrado - Resumen")
    @Post(value = "/resume/{nominaPeriodoId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> resumen(@PathVariable Integer nominaPeriodoId, @Body List<Integer> lista){
        try {
            return HttpResponse.ok(timbradoService.resumen(nominaPeriodoId,lista));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.timbrado.prueba.resumen.resumen}",
            description = "${cosmonaut.controller.timbrado.prueba.resumen.descripcion}",
            operationId = "timbrado.prueba.resumen")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Timbrado - Prueba Sawagger")
    @Get(value = "/prueba/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<String> prueba(){
        return HttpResponse.ok("Actualiza");

    }
}
