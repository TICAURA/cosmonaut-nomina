package mx.com.ga.cosmonaut.nomina.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.CalculoNetoBruto;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.CalculoSalarioImssCompletoPpp;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.NominaCalculadora;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.NominaCalculo;
import mx.com.ga.cosmonaut.orquestador.service.CalculadoraService;

import javax.inject.Inject;

@Controller("/calculo")
public class CalculoController {

    @Inject
    private CalculadoraService calculadoraService;

    @Operation(summary = "${cosmonaut.controller.calculo.salario.brutomensual.resumen}",
            description = "${cosmonaut.controller.calculo.salario.brutomensual.descripcion}",
            operationId = "calculo.salario.brutomensual")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cálculo - Servicio de cálculo de sueldo bruto a neto")
    @Post(value = "/salario/bruto/mensual/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> calculoSalarioBrutoMensual(@Body NominaCalculo nominaCalculo){
        try {
            return HttpResponse.ok(calculadoraService.calculoSalarioBrutoNeto(nominaCalculo));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.calculo.salario.imsscompletoppp.resumen}",
            description = "${cosmonaut.controller.calculo.salario.imsscompletoppp.descripcion}",
            operationId = "calculo.salario.imsscompletoppp")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cálculo - Salario IMSS y Completo PPP")
    @Post(value = "/salario/imss/completo/ppp", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> calculoSalarioImssCompletoPpp(@Body CalculoSalarioImssCompletoPpp calculoSalarioImssCompletoPpp){
        try {
            return HttpResponse.ok(calculadoraService.calculoSalarioImssCompletoPpp(calculoSalarioImssCompletoPpp));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.calculo.sueldonetosueldobruto.resumen}",
            description = "${cosmonaut.controller.calculo.sueldonetosueldobruto.descripcion}",
            operationId = "calculo.sueldonetosueldobruto")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cálculo - Servicio de cálculo de sueldo neto a sueldo bruto")
    @Post(value = "/sueldo_neto/sueldo_bruto/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> calculoSueldoNetoSueldoBruto(@Body CalculoNetoBruto calculo){
        try {
            return HttpResponse.ok(calculadoraService.calculoSueldoNetoSueldoBruto(calculo));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.calculo.sueldonetosueldobruto.resumen}",
            description = "${cosmonaut.controller.calculo.sueldonetosueldobruto.descripcion}",
            operationId = "calculo.sueldonetosueldobruto")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Cálculo - Servicio de cálculo de sueldo neto a sueldo bruto")
    @Post(value = "/sueldo/neto/sueldo/bruto/semanal", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> calculoSalarioBrutoSalarioNeto(@Body NominaCalculadora calculo){
        try {
            return HttpResponse.ok(calculadoraService.calculoSalarioBrutoNetoCalculadora(calculo));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
}
