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
import mx.com.ga.cosmonaut.orquestador.service.CalculaNominaLiquidacionService;
import mx.com.ga.cosmonaut.orquestador.service.NominaLiquidacionLibService;
import mx.com.ga.cosmonaut.orquestador.service.NominaService;

import javax.inject.Inject;

@Controller("/nomina-liquidacion")
public class NominaLiquidacionController {

    @Inject
    private NominaLiquidacionLibService nominaLiquidacionLibService;

    @Inject
    private NominaService nominaService;

    @Inject
    private CalculaNominaLiquidacionService calculaNominaLiquidacionService;

    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.lista.nominaliquidacion.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.lista.nominaliquidacion.descripcion}",
            operationId = "nominaliquidacion.lista.nominaliquidacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Listado de nómina de liquidación")
    @Post(value = "/lista/nomina/liquidacion/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaNominaLiquidacion(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaLiquidacionLibService.listaNominaLiquidacion(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.calcula.nominaliquidacion.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.calcula.nominaliquidacion.descripcion}",
            operationId = "nominaliquidacion.calcula.nominaliquidacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Calcular nómina liquidación")
    @Post(value = "/calcula/nomina/liquidacion/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> calcula(@Header("datos-flujo") String datosFlujo,
                                                   @Header("datos-sesion") String datosSesion,
                                                    @Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaService.calcular(nomina.getNominaXperiodoId()));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.lista.empleadostotalpagonetoliquidacion.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.lista.empleadostotalpagonetoliquidacion.descripcion}",
            operationId = "nominaliquidacion.lista.empleadostotalpagonetoliquidacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Listado de empleados con total de pago neto de liquidación")
    @Post(value = "/lista/empleados/total/pago/neto/liquidacion/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadosTotalPagoNetoLiquidacion(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaLiquidacionLibService.listaEmpleadosTotalPagoNetoLiquidacion(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.detalle.liquidacionempleado.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.detalle.liquidacionempleado.descripcion}",
            operationId = "nominaliquidacion.detalle.liquidacionempleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Detalle de liquidación por empleado")
    @Post(value = "/detalle/liquidacion/empleado/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> detalleLiquidacionEmpleado(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaLiquidacionLibService.detalleLiquidacionEmpleado(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    /**
    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.desglose.salariodiariointegradofiniquitoliquidacion.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.desglose.salariodiariointegradofiniquitoliquidacion.descripcion}",
            operationId = "nominaliquidacion.desglose.salariodiariointegradofiniquitoliquidacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Desglose del salario diario integrado liquidación/finiquito")
    @Post(value = "/desglose/salario/diario/integrado/finiquito-liquidacion", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> desgloseSalarioDiarioIntegradoFiniquitoLiquidacion(@Body Nomina nomina){
        try {
            return HttpResponse.ok(OrquestadorUtil.generaRespuesta(nominaLiquidacionService.desgloseSalarioDiarioIntegradoFiniquitoLiquidacion(
                    nomina, OrquestadorConstantes.ORQUESTADOR_CVE_90)));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.desglose.impuestosobrenominafiniquitoliquidacion.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.desglose.impuestosobrenominafiniquitoliquidacion.descripcion}",
            operationId = "nominaliquidacion.desglose.impuestosobrenominafiniquitoliquidacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Desglose del impuesto sobre nómina de liquidación/finiquito")
    @Post(value = "/desglose/impuesto/sobre/nomina/liquidacion/finiquito-liquidacion", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> desgloseImpuestoSobreNominaFiniquitoLiquidacion(@Body Nomina nomina){
        try {
            return HttpResponse.ok(OrquestadorUtil.generaRespuesta(nominaLiquidacionService.desgloseImpuestoSobreNominaFiniquitoLiquidacion(
                    nomina, OrquestadorConstantes.ORQUESTADOR_CVE_91)));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
     */
    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.lista.empleadosfechabajapercepcionesdeduccionesliquidacion.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.lista.empleadosfechabajapercepcionesdeduccionesliquidacion.descripcion}",
            operationId = "nominaliquidacion.lista.empleadosfechabajapercepcionesdeduccionesliquidacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Listado de empleados con fecha baja, percepciones, deducciones, tipo de pago y total neto de liquidación")
    @Post(value = "/lista/empleados/fechabaja/percepciones/deducciones/liquidacion/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadosFechaBajaPercepcionesDeduccionesLiquidacion(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaLiquidacionLibService.listaEmpleadosFechaBajaPercepcionesDeduccionesLiquidacion(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    /**
    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.descarga.dispersionliquidacion.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.descarga.dispersionliquidacion.descripcion}",
            operationId = "nominaliquidacion.descarga.dispersionliquidacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Descarga de dispersión de liquidación")
    @Post(value = "/descarga/dispersion/liquidacion/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> descargaDispersionLiquidacion(@Body Nomina nomina){
        try {
            return HttpResponse.ok(OrquestadorUtil.generaRespuesta(nominaLiquidacionService.descargaDispersionLiquidacion(
                    nomina, OrquestadorConstantes.ORQUESTADOR_CVE_94)));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
*/
    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.lista.empleadostotalpagonetotimbradoliquidacion.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.lista.empleadostotalpagonetotimbradoliquidacion.descripcion}",
            operationId = "nominaliquidacion.lista.empleadostotalpagonetotimbradoliquidacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Listado de empleados con total de pago neto y timbrado de liquidación")
    @Post(value = "/lista/empleados/total/pagoneto/timbrado/liquidacion/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadosTotalPagoNetoTimbradoLiquidacion(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaLiquidacionLibService.listaEmpleadosTotalPagoNetoTimbradoLiquidacion(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.detalle.empleadostotalpagonetodetallemontostimbradoliquidacion.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.detalle.empleadostotalpagonetodetallemontostimbradoliquidacion.descripcion}",
            operationId = "nominaliquidacion.detalle.empleadostotalpagonetodetallemontostimbradoliquidacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Detalle de empleados con total de pago neto y detalle de montos para timbrado de liquidación")
    @Post(value = "/detalle/empleados/total/pagoneto/detalle/montos/timbrado/liquidacion/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> detalleEmpleadosTotalPagoNetoDetalleMontosTimbradoLiquidacion(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaLiquidacionLibService.detalleEmpleadosTotalPagoNetoDetalleMontosTimbradoLiquidacion(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
/**
    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.descarga.recibosliquidacion.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.descarga.recibosliquidacion.descripcion}",
            operationId = "nominaliquidacion.descarga.recibosliquidacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Descargar recibos de liquidación")
    @Post(value = "/descarga/recibos/liquidacion/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> descargaRecibosLiquidacion(@Body Nomina nomina){
        try {
            return HttpResponse.ok(OrquestadorUtil.generaRespuesta(nominaLiquidacionService.descargaRecibosLiquidacion(
                    nomina, OrquestadorConstantes.ORQUESTADOR_CVE_99)));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
*/

    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.guardar.nominaliquidacion.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.guardar.nominaliquidacion.descripcion}",
            operationId = "nominaliquidacion.guardar.nominaliquidacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Guardar nómina Liquidación")
    @Post(value = "/guardar/nomina/liquidacion/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> crear(@Header("datos-flujo") String datosFlujo,
                                                 @Header("datos-sesion") String datosSesion,
                                                 @Body NominaExtraordinaria nominaExtraordinaria){
        try {
            return HttpResponse.ok(nominaLiquidacionLibService.guardarNominaLiquidacion(nominaExtraordinaria));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.guardar.nominafiniquitoliquidacion.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.guardar.nominafiniquitoliquidacion.descripcion}",
            operationId = "nominaliquidacion.guardar.nominafiniquitoliquidacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Guardar nómina Finiquito/Liquidacion")
    @Post(value = "/guardar/nomina/finiquito-liquidacion/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> guardarNominaFiniquitoLiquidacion(@Header("datos-flujo") String datosFlujo,
                                                                             @Header("datos-sesion") String datosSesion,
                                                                             @Body NominaExtraordinaria nominaExtraordinaria){
        try {
            return HttpResponse.ok(nominaLiquidacionLibService.guardarNominaFiniquitoLiquidacion(nominaExtraordinaria));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.calcula.nominafiniquitoliquidacion.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.calcula.nominafiniquitoliquidacion.descripcion}",
            operationId = "nominaliquidacion.calcula.nominafiniquitoliquidacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Calcula nómina Finiquito/Liquidacion")
    @Post(value = "/calcula/nomina/finiquito-liquidacion/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> calculaNominaFiniquitoLiquidacion(@Header("datos-flujo") String datosFlujo,
                                                                             @Header("datos-sesion") String datosSesion,
                                                                             @Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaService.calcular(nomina.getNominaXperiodoId()));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.eliminacion.nominaliquidacion.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.eliminacion.nominaliquidacion.descripcion}",
            operationId = "nominaliquidacion.eliminacion.nominaliquidacion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Eliminacion nómina Liquidacion")
    @Post(value = "/eliminacion/nomina/liquidacion/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminar(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaLiquidacionLibService.eliminacionNominaLiquidacion(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.lista.empleadostotalpagonetoliquidacionfiltrar.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.lista.empleadostotalpagonetoliquidacionfiltrar.descripcion}",
            operationId = "nominaliquidacion.lista.empleadostotalpagonetoliquidacionfiltrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Listado de empleados con total de pago neto de liquidación filtrar")
    @Post(value = "/lista/empleados/total/pago/neto/liquidacion/filtrar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadosTotalPagoNetoLiquidacionFiltrar(@Body NominaFiltrado nomina){
        try {
            return HttpResponse.ok(nominaLiquidacionLibService.listaEmpleadosTotalPagoNetoLiquidacionFiltrar(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.lista.empleadosfechabajapercepcionesdeduccionesliquidacionfiltrar.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.lista.empleadosfechabajapercepcionesdeduccionesliquidacionfiltrar.descripcion}",
            operationId = "nominaliquidacion.lista.empleadosfechabajapercepcionesdeduccionesliquidacionfiltrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Listado de empleados con fecha baja, percepciones, deducciones, tipo de pago y total neto de liquidación filtrar")
    @Post(value = "/lista/empleados/fechabaja/percepciones/deducciones/liquidacion/filtrar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadosFechaBajaPercepcionesDeduccionesLiquidacionFiltrar(@Body NominaFiltrado nomina){
        try {
            return HttpResponse.ok(nominaLiquidacionLibService.listaEmpleadosFechaBajaPercepcionesDeduccionesLiquidacionFiltrar(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.lista.empleadostotalpagonetotimbradoliquidacionfiltrar.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.lista.empleadostotalpagonetotimbradoliquidacionfiltrar.descripcion}",
            operationId = "nominaliquidacion.lista.empleadostotalpagonetotimbradoliquidacionfiltrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Listado de empleados con total de pago neto y timbrado de liquidación filtrar")
    @Post(value = "/lista/empleados/total/pagoneto/timbrado/liquidacion/filtrar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadosTotalPagoNetoTimbradoLiquidacionFiltrar(@Body NominaFiltrado nomina){
        try {
            return HttpResponse.ok(nominaLiquidacionLibService.listaEmpleadosTotalPagoNetoTimbradoLiquidacionFiltrar(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.recalcula.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.recalcula.descripcion}",
            operationId = "nominaliquidacion.recalcula")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Calcular nómina liquidación")
    @Post(value = "/recalcula/nomina/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> recalcular(@Header("datos-flujo") String datosFlujo,
                                                      @Header("datos-sesion") String datosSesion,
                                                      @Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaService.calcular(nomina.getNominaXperiodoId()));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaliquidacion.lista.nominaliquidacionpaginado.resumen}",
            description = "${cosmonaut.controller.nominaliquidacion.lista.nominaliquidacionpaginado.descripcion}",
            operationId = "nominaliquidacion.lista.nominaliquidacionpaginado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Liquidación - Listado de nómina de liquidación")
    @Post(value = "/lista/nomina/liquidacion/paginado/{numeroRegistros}/{pagina}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaNominaLiquidacionPaginado(@Body Nomina nomina,
                                                                  @PathVariable Integer numeroRegistros,
                                                                  @PathVariable Integer pagina){
        try {
            return HttpResponse.ok(nominaLiquidacionLibService.listaNominaLiquidacionPaginado(nomina,numeroRegistros,pagina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Post(value = "/recalculo/pruebas/{id}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> recalculoPruebas(@PathVariable Integer id){
        try {
            return HttpResponse.ok(calculaNominaLiquidacionService.reCalculoNomina(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
