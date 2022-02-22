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
import mx.com.ga.cosmonaut.orquestador.dto.peticion.VariabilidadPeticion;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.orquestador.service.VariabilidadService;

import javax.inject.Inject;

@Controller("/variabilidad")
public class VariabilidadController {

    @Inject
    private VariabilidadService variabilidadLibService;

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.variabilidad.calculo.promediovariables.resumen}",
            description = "${cosmonaut.controller.variabilidad.calculo.promediovariables.descripcion}",
            operationId = "variabilidad.calculo.promediovariables")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Variabilidad - Cálculo promedio de variables")
    @Post(value = "/calculo/promedio/variables", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> calculoPromedioVariables(@Header("datos-flujo") String datosFlujo,
                                                                    @Header("datos-sesion") String datosSesion,
                                                                    @Body VariabilidadPeticion VariabilidadPeticion){
        try {
            return HttpResponse.ok(variabilidadLibService.calculoPromedioVariables(VariabilidadPeticion));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.variabilidad.recalculo.promediovariables.resumen}",
            description = "${cosmonaut.controller.variabilidad.recalculo.promediovariables.descripcion}",
            operationId = "variabilidad.recalculo.promediovariables")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Variabilidad - Recalculo promedio de variables")
    @Get(value = "/recalculo/promedio/variables/{id}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> recalculoPromedioVariables(@Header("datos-flujo") String datosFlujo,
                                                                      @Header("datos-sesion") String datosSesion,
                                                                      @PathVariable Integer id){
        try {
            return HttpResponse.ok(variabilidadLibService.recalculoPromedioVariables(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.variabilidad.aplicar.promediovariables.resumen}",
            description = "${cosmonaut.controller.variabilidad.aplicar.promediovariables.descripcion}",
            operationId = "variabilidad.aplicar.promediovariables")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Variabilidad - Aplicar promedio de variables")
    @Post(value = "/aplicar/promedio/variables", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> aplicarPromedioVariables(@Header("datos-flujo") String datosFlujo,
                                                                    @Header("datos-sesion") String datosSesion,
                                                                    @Body VariabilidadPeticion variabilidad){
        try {
            return HttpResponse.ok(variabilidadLibService.aplicarPromedioVariables(variabilidad));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.variabilidad.lista.empleadospromediovariables.resumen}",
            description = "${cosmonaut.controller.variabilidad.lista.empleadospromediovariables.descripcion}",
            operationId = "variabilidad.lista.empleadospromediovariables")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Variabilidad - Lista de empleados con promedio de variables")
    @Post(value = "/lista/empleados/promedio/variables", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadosPromedioVariables(@Body VariabilidadPeticion variabilidad){
        try {
            return HttpResponse.ok(variabilidadLibService.listaEmpleadosPromedioVariables(variabilidad));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
