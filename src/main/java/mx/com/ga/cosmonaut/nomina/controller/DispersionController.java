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
import mx.com.ga.cosmonaut.nomina.dto.dispercion.peticion.EmpleadoDispersion;
import mx.com.ga.cosmonaut.nomina.dto.dispercion.peticion.RfcPeticion;
import mx.com.ga.cosmonaut.nomina.service.DispersionServices;

import javax.inject.Inject;
import java.util.List;

@Controller("/dispersion")
public class DispersionController {

    @Inject
    private DispersionServices dispersionServices;

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.dispersion.resumen}",
            description = "${cosmonaut.controller.dispersion.descripcion}",
            operationId = "dispersion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Dispersion - Dispersion")
    @Put(value = "/{nominaPeriodoId}/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> dispersion(@Header("datos-flujo") String datosFlujo,
                                                      @Header("datos-sesion") String datosSesion,
                                                      @Body List<DispersionDto> listaDispersionDto,
                                                      @PathVariable Integer nominaPeriodoId){
        try {
            return HttpResponse.ok(dispersionServices.dispersion(nominaPeriodoId,listaDispersionDto));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.dispersion.procesando.resumen}",
            description = "${cosmonaut.controller.dispersion.procesando.descripcion}",
            operationId = "dispersion.procesando")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Dispersion - Procesando")
    @Post(value = "/procesando/{nominaPeriodoId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> procesando(@PathVariable Integer nominaPeriodoId, @Body List<Long> lista){
        try {
            return HttpResponse.ok(dispersionServices.procesando(nominaPeriodoId, lista));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.dispersion.respuesta.resumen}",
            description = "${cosmonaut.controller.dispersion.respuesta.descripcion}",
            operationId = "dispersion.respuestabanpay")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Dispersion - Respuesta Dispersion")
    @Post(value = "/respuesta/banpay", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> respuestaBANPAY(@Body String respuesta){
        try {
            return HttpResponse.ok(dispersionServices.respuestaBANPAY(respuesta));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.dispersion.respuesta.resumen}",
            description = "${cosmonaut.controller.dispersion.respuesta.descripcion}",
            operationId = "dispersion.respuestastp")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Dispersion - Respuesta Dispersion")
    @Post(value = "/respuesta/stp", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> respuestaSTP(@Body String respuesta){
        try {
            return HttpResponse.ok(dispersionServices.respuestaSTP(respuesta));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.dispersion.respuesta.resumen}",
            description = "${cosmonaut.controller.dispersion.respuesta.descripcion}")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Tag(name = "Dispersion - Respuesta Dispersion")
    @Post(value = "/respuesta/complementaria/stp", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> respuestaComplementariaSTP(@Body String respuesta){
        try {
            return HttpResponse.ok(dispersionServices.respuestaComplementariaSTP(respuesta));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
    @Operation(summary = "${cosmonaut.controller.dispersion.rfc.resumen}",
            description = "${cosmonaut.controller.dispersion.rfc.descripcion}",
            operationId = "dispersion.rfc")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Dispersion - Consulta RFC")
    @Post(value = "/consulta/rfc", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> obtenerRfc(@Body RfcPeticion rfcPeticion){
        try {
            return HttpResponse.ok(dispersionServices.obtenerRfc(rfcPeticion));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.dispersion.resumen.resumen}",
            description = "${cosmonaut.controller.dispersion.resumen.descripcion}",
            operationId = "dispersion.resumen")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Dispersion - Resumen de dispersiones")
    @Post(value = "/resume/{nominaPeriodoId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> resumen(@PathVariable Integer nominaPeriodoId, @Body List<Long> lista){
        try {
            return HttpResponse.ok(dispersionServices.resumen(nominaPeriodoId,lista));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.dispersion.modificaempleadodispersado.resumen.resumen}",
            description = "${cosmonaut.controller.dispersion.modificaempleadodispersado.resumen.descripcion}",
            operationId = "dispersion.modificaempleadodispersado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Dispersion - Modifica Empleado dispersado")
    @Post(value = "/modifica/estatus/empleado/dispersado/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> modificarEstatusEmpleadoDispersion(@Header("datos-flujo") String datosFlujo,
                                                                              @Header("datos-sesion") String datosSesion,
                                                                              @Body List<EmpleadoDispersion> empleadoDispersion){
        try {
            return HttpResponse.ok(dispersionServices.modificarEstatusEmpleadoDispersion(empleadoDispersion));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    
}
