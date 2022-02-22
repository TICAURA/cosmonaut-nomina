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
import mx.com.ga.cosmonaut.nomina.interceptor.BitacoraNomina;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.Nomina;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.NominaExtraordinaria;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.NominaFiltrado;
import mx.com.ga.cosmonaut.orquestador.service.NominaExtraordinariaLibService;
import mx.com.ga.cosmonaut.orquestador.service.NominaService;

import javax.inject.Inject;

@Controller("/nomina-extraordinaria")
public class NominaExtraordinariaController {

    @Inject
    private NominaExtraordinariaLibService nominaExtraordinariaLibService;

    @Inject
    private NominaService nominaService;

    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.consulta.nominaextraordinaria.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.consulta.nominaextraordinaria.descripcion}",
            operationId = "nominaextraordinaria.consulta.nominaextraordinaria")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Consulta nómina Extraordinaria")
    @Post(value = "/consulta/nomina/extraordinaria/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> consultaNominaExtraordinaria(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaExtraordinariaLibService.consultaNominaExtraordinaria(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }


    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.calculo.nominaextraordinariaaguinaldo.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.calculo.nominaextraordinariaaguinaldo.descripcion}",
            operationId = "nominaextraordinaria.calculo.nominaextraordinariaaguinaldo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Calculo nómina Extraordinaria Aguinaldo")
    @Post(value = "/calculo/nomina/extraordinaria/aguinaldo/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> calcular(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaService.calcular(nomina.getNominaXperiodoId()));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.lista.empleadoaguinaldo.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.lista.empleadoaguinaldo.descripcion}",
            operationId = "nominaextraordinaria.lista.empleadoaguinaldo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Lista Empleado Aguinaldo")
    @Post(value = "/lista/empleado/aguinaldo/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoAguinaldo(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaExtraordinariaLibService.listaEmpleadoAguinaldo(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.detalle.nominaempleadoaguinaldo.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.detalle.nominaempleadoaguinaldo.descripcion}",
            operationId = "nominaextraordinaria.detalle.nominaempleadoaguinaldo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Detalle nómina Empleado Aguinaldo")
    @Post(value = "/detalle/nomina/empleado/aguinaldo/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> detalleNominaEmpleadoAguinaldo(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaExtraordinariaLibService.detalleNominaEmpleadoAguinaldo(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.reporte.nominaextraordinaria.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.reporte.nominaextraordinaria.descripcion}",
            operationId = "nominaextraordinaria.reporte.nominaextraordinaria")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Reporte de nómina extraordinaria (extraordinaria)")
    @Post(value = "/reporte/nomina/extraordinaria", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> reporteNominaExtraordinaria(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaExtraordinariaLibService.reporteNominaExtraordinaria(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.lista.empleadopagonetonominaextraordinaria.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.lista.empleadopagonetonominaextraordinaria.descripcion}",
            operationId = "nominaextraordinaria.lista.empleadopagonetonominaextraordinaria")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Lista Empleado Pago Neto nómina Extraordinaria")
    @Post(value = "/lista/empleado/pago/neto/nomina/extraordinaria", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoPagoNetoNominaExtraordinaria(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaExtraordinariaLibService.listaEmpleadoPagoNetoNominaExtraordinaria(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
/**
    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.descarga.dispersionnominaextraordinariaaguinaldo.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.descarga.dispersionnominaextraordinariaaguinaldo.descripcion}",
            operationId = "nominaextraordinaria.descarga.dispersionnominaextraordinariaaguinaldo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Descarga Dispersion nómina Extraordinaria Aguinaldo")
    @Post(value = "/descarga/dispersion/nomina/extraordinaria/aguinaldo", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> descargaDispersionNominaExtraordinariaAguinaldo(@Body Nomina nomina){
        try {
            return HttpResponse.ok(OrquestadorUtil.generaRespuesta(nominaExtraordinariaService.descargaDispersionNominaExtraordinariaAguinaldo(
                    nomina, OrquestadorConstantes.ORQUESTADOR_CVE_35)));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
*/
    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.lista.empleadorotalpagonetonominaextraordinariaaguinaldotimbrado.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.lista.empleadorotalpagonetonominaextraordinariaaguinaldotimbrado.descripcion}",
            operationId = "nominaextraordinaria.lista.empleadorotalpagonetonominaextraordinariaaguinaldotimbrado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Lista Empleado Total Pago Neto nómina Extraordinaria Aguinaldo Timbrado")
    @Post(value = "/lista/empleado/total/pago/neto/nomina/extraordinaria/aguinaldo/timbrado", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoTotalPagoNetoNominaExtraordinariaAguinaldoTimbrado(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaExtraordinariaLibService.listaEmpleadoTotalPagoNetoNominaExtraordinariaAguinaldoTimbrado(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.detalle.empleadototalpagonetomontosnominaextraordinaria.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.detalle.empleadototalpagonetomontosnominaextraordinaria.descripcion}",
            operationId = "nominaextraordinaria.detalle.empleadototalpagonetomontosnominaextraordinaria")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Detalle Empleado Total Pago Neto Montos nómina Extraordinaria")
    @Post(value = "/detalle/empleado/total/pago/neto/montos/nomina/extraordinaria", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> detalleEmpleadoTotalPagoNetoMontosNominaExtraordinaria(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaExtraordinariaLibService.detalleEmpleadoTotalPagoNetoMontosNominaExtraordinaria(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
/**
    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.descarga.recibosaguinaldonominaextraordinaria.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.descarga.recibosaguinaldonominaextraordinaria.descripcion}",
            operationId = "nominaextraordinaria.descarga.recibosaguinaldonominaextraordinaria")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Descarga de recibos de aguinaldo nómina extraordinaria")
    @Post(value = "/descarga/recibos/aguinaldo/nomina/extraordinaria", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> descargaRecibosAguinaldoNominaExtraordinaria(@Body Nomina nomina){
        try {
            return HttpResponse.ok(OrquestadorUtil.generaRespuesta(nominaExtraordinariaService.descargaRecibosAguinaldoNominaExtraordinaria(
                    nomina, OrquestadorConstantes.ORQUESTADOR_CVE_39)));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    } correccion del flujo
*/
    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.guardar.nominaextraordinaria.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.guardar.nominaextraordinaria.descripcion}",
            operationId = "nominaextraordinaria.guardar.nominaextraordinaria")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Guardar nómina extraordinaria")
    @Post(value = "/guardar/nomina/extraordinaria/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> crear(@Header("datos-flujo") String datosFlujo,
                                                 @Header("datos-sesion") String datosSesion,
                                                 @Body  NominaExtraordinaria nominaExtraordinaria){
        try {
            return HttpResponse.ok(nominaExtraordinariaLibService.guardarNominaExtraordinaria(nominaExtraordinaria));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.eliminacion.nominaaguinaldo.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.eliminacion.nominaaguinaldo.descripcion}",
            operationId = "nominaextraordinaria.eliminacion.nominaaguinaldo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Eliminación nómina Aguinaldo")
    @Post(value = "/eliminacion/nomina/aguinaldo", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminar(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaService.eliminarNomina(nomina.getNominaXperiodoId()));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.lista.empleadoaguinaldofiltrar.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.lista.empleadoaguinaldofiltrar.descripcion}",
            operationId = "nominaextraordinaria.lista.empleadoaguinaldofiltrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Lista Empleado Aguinaldo filtrar")
    @Post(value = "/lista/empleado/aguinaldo/filtrar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoAguinaldoFiltrar(@Body NominaFiltrado nomina){
        try {
            return HttpResponse.ok(nominaExtraordinariaLibService.listaEmpleadoAguinaldoFiltrar(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.lista.empleadopagonetonominaextraordinariafiltrar.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.lista.empleadopagonetonominaextraordinariafiltrar.descripcion}",
            operationId = "nominaextraordinaria.lista.empleadopagonetonominaextraordinariafiltrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Lista Empleado Pago Neto nómina Extraordinaria filtrar")
    @Post(value = "/lista/empleado/pago/neto/nomina/extraordinaria/filtrar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoPagoNetoNominaExtraordinariaFiltrar(@Body NominaFiltrado nomina){
        try {
            return HttpResponse.ok(nominaExtraordinariaLibService.listaEmpleadoPagoNetoNominaExtraordinariaFiltrar(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.lista.empleadorotalpagonetonominaextraordinariaaguinaldotimbradofiltrar.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.lista.empleadorotalpagonetonominaextraordinariaaguinaldotimbradofiltrar.descripcion}",
            operationId = "nominaextraordinaria.lista.empleadorotalpagonetonominaextraordinariaaguinaldotimbradofiltrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Lista Empleado Total Pago Neto nómina Extraordinaria Aguinaldo Timbrado filtrar")
    @Post(value = "/lista/empleado/total/pago/neto/nomina/extraordinaria/aguinaldo/timbrado/filtrar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoTotalPagoNetoNominaExtraordinariaAguinaldoTimbradoFiltrar(@Body NominaFiltrado nomina){
        try {
            return HttpResponse.ok(nominaExtraordinariaLibService.listaEmpleadoTotalPagoNetoNominaExtraordinariaAguinaldoTimbradoFiltrar(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.recalculo.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.recalculo.descripcion}",
            operationId = "nominaextraordinaria.recalculo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Recalculo")
    @Post(value = "/recalculo", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> recalcular(@Header("datos-flujo") String datosFlujo,
                                                      @Header("datos-sesion") String datosSesion,
                                                      @Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaService.calcular(nomina.getNominaXperiodoId()));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaextraordinaria.lista.empleadoaguinaldopaginado.resumen}",
            description = "${cosmonaut.controller.nominaextraordinaria.lista.empleadoaguinaldopaginado.descripcion}",
            operationId = "nominaextraordinaria.lista.empleadoaguinaldopaginado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina extraordinaria - Lista Empleado Aguinaldo")
    @Post(value = "/lista/empleado/aguinaldo/paginado/{numeroRegistros}/{pagina}", consumes = MediaType.APPLICATION_JSON, 
            processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoAguinaldoPaginado(@Body Nomina nomina,
                                                                          @PathVariable Integer numeroRegistros,
                                                                          @PathVariable Integer pagina){
        try {
            return HttpResponse.ok(nominaExtraordinariaLibService.listaEmpleadoAguinaldoPaginado(nomina, numeroRegistros, pagina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
