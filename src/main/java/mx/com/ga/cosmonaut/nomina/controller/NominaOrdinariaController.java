package mx.com.ga.cosmonaut.nomina.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.sse.Event;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.interceptor.BitacoraSistema;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.nomina.interceptor.BitacoraNomina;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.Nomina;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.NominaFiltrado;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.NominaOrdinaria;
import mx.com.ga.cosmonaut.orquestador.dto.respuesta.RespuestaAsyncNomina;
import mx.com.ga.cosmonaut.orquestador.service.CalculoNominaOrdinariaServices;
import mx.com.ga.cosmonaut.orquestador.service.NominaOrdinariaLibService;
import mx.com.ga.cosmonaut.orquestador.service.NominaService;
import org.reactivestreams.Publisher;

import javax.inject.Inject;

@Controller("/nomina-ordinaria")
public class NominaOrdinariaController {

    @Inject
    private NominaOrdinariaLibService nominaOrdinariaLibService;

    @Inject
    private NominaService nominaService;

    @Inject
    private CalculoNominaOrdinariaServices calculoNominaOrdinariaServices;

    @Operation(summary = "${cosmonaut.controller.nominaordinaria.consulta.nominasactivas.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.consulta.nominasactivas.descripcion}",
            operationId = "nominaordinaria.consulta.nominasactivas")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Consulta nominas activas ordenadas en forma ascendente")
    @Post(value = "/consulta/nominas/activas/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> consultaNominasActivas(@Body Nomina nomina){
        try {
            //ordenamiento por id de nomina
            return HttpResponse.ok(nominaOrdinariaLibService.consultaNominasActivas(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaordinaria.calculo.nominaperiodo.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.calculo.nominaperiodo.descripcion}",
            operationId = "nominaordinaria.calculo.nominaperiodo")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Calcula nominas periodo")
    @Post(value = "/calcula/nomina/periodo/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> calcular(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaService.calcular(nomina.getNominaXperiodoId()));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaordinaria.calculo.nominaperiodo.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.calculo.nominaperiodo.descripcion}")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Tag(name = "Nómina Ordinaria - Calcula nominas periodo (async)")
    @Post(value = "/calcula/nomina/periodo/async", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public Publisher<Event<RespuestaAsyncNomina>> suscribirCalculoNomina(@Header("datos-flujo") String datosFlujo,
                                                                         @Header("datos-sesion") String datosSesion,
                                                                         @Body Nomina nomina) {
        PublishSubject<Event<RespuestaAsyncNomina>> publisher = PublishSubject.create();
        Flowable<Event<RespuestaAsyncNomina>> flowable = publisher.toFlowable(BackpressureStrategy.BUFFER);
        calculoNominaOrdinariaServices.calculoNominaOrdinariaAsync(nomina.getNominaXperiodoId(), publisher);
        return flowable;
    }

    @Operation(summary = "${cosmonaut.controller.nominaordinaria.recalcula.nomina.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.recalcula.nomina.descripcion}")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON))
    @Tag(name = "Nómina Ordinaria - Recalcula nomina (async)")
    @Post(value = "/recalcula/nomina/async", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public Publisher<Event<RespuestaAsyncNomina>> suscribirRecalculoNomina(@Header("datos-flujo") String datosFlujo,
                                                                           @Header("datos-sesion") String datosSesion,
                                                                           @Body Nomina nomina){
        PublishSubject<Event<RespuestaAsyncNomina>> publisher = PublishSubject.create();
        Flowable<Event<RespuestaAsyncNomina>> flowable = publisher.toFlowable(BackpressureStrategy.BUFFER);
        calculoNominaOrdinariaServices.reCalculoNominaAsync(nomina.getNominaXperiodoId(), publisher);
        return flowable;
    }

    @Operation(summary = "${cosmonaut.controller.nominaordinaria.lista.empleadocalculopercepcionesdeducciones.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.lista.empleadocalculopercepcionesdeducciones.descripcion}",
            operationId = "nominaordinaria.lista.empleadocalculopercepcionesdeducciones")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Lista empleado calculo percepciones deducciones")
    @Post(value = "/lista/empleado/calculo/percepciones/deducciones/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoCalculoPercepcionesDeducciones(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaOrdinariaLibService.listaEmpleadoCalculoPercepcionesDeducciones(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaordinaria.detalle.nominaempleado.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.detalle.nominaempleado.descripcion}",
            operationId = "nominaordinaria.detalle.nominaempleado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Detalle nomina empleado")
    @Post(value = "/detalle/nomina/empleado/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> detalleNominaEmpleado(@Body  Nomina nomina){
        try {
            return HttpResponse.ok(nominaOrdinariaLibService.detalleNominaEmpleado(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
    

    @Operation(summary = "${cosmonaut.controller.nominaordinaria.detalle.montosimsspatronal.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.detalle.montosimsspatronal.descripcion}",
            operationId = "nominaordinaria.detalle.montosimsspatronal")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Montos de IMSS Patronal")
    @Post(value = "/detalle/montos/imms/patronal/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> detalleMontosImssPatronal(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaOrdinariaLibService.detalleMontosImssPatronal(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaordinaria.lista.empleadototalpagoneto.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.lista.empleadototalpagoneto.descripcion}",
            operationId = "nominaordinaria.lista.empleadototalpagoneto")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Lista empleado total pago neto")
    @Post(value = "/lista/empleado/total/pago/neto/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoTotalPagoNeto(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaOrdinariaLibService.listaEmpleadoTotalPagoNeto(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaordinaria.reporte.nomina.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.reporte.nomina.descripcion}",
            operationId = "nominaordinaria.reporte.nomina")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Reporte de nómina")
    @Post(value = "/reporte/nomina", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> reporteNomina(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaOrdinariaLibService.reporteNomina(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaordinaria.descarga.dispercion.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.descarga.dispercion.descripcion}",
            operationId = "nominaordinaria.descarga.dispercion")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Descarga dispersion")
    @Post(value = "/descarga/dispercion/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> descargaDispersion(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaOrdinariaLibService.descargaDispersion(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaordinaria.lista.empleadototalpagonetofechatimbrado.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.lista.empleadototalpagonetofechatimbrado.descripcion}",
            operationId = "nominaordinaria.lista.empleadototalpagonetofechatimbrado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Listado empleado total pago neto fecha timbrado")
    @Post(value = "/listado/empleado/total/pago/neto/fecha/timbrado/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listadoEmpleadoTotalPagoNetoFechaTimbrado(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaOrdinariaLibService.listadoEmpleadoTotalPagoNetoFechaTimbrado(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaordinaria.detalle.empleadototalpagonetodetallemontostimbrado.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.detalle.empleadototalpagonetodetallemontostimbrado.descripcion}",
            operationId = "nominaordinaria.detalle.empleadototalpagonetodetallemontostimbrado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Detalle empleado total pago neto detalle montos timbrado")
    @Post(value = "/detalle/empleado/total/pago/neto/detalle/monto/timbrado/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> detalleEmpleadoTotalPagoNetoDetalleMontosTimbrado(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaOrdinariaLibService.detalleEmpleadoTotalPagoNetoDetalleMontosTimbrado(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaordinaria.guardar.nomina.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.guardar.nomina.descripcion}",
            operationId = "nominaordinaria.guardar.nomina")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Guardar nómina")
    @Post(value = "/guardar/nomina/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> crear(@Header("datos-flujo") String datosFlujo,
                                                 @Header("datos-sesion") String datosSesion,
                                                 @Body NominaOrdinaria nominaOrdinaria){
        try {
            return HttpResponse.ok(nominaOrdinariaLibService.guardarNomina(nominaOrdinaria));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaordinaria.eliminacion.nominaoridnaria.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.eliminacion.nominaoridnaria.descripcion}",
            operationId = "nominaordinaria.eliminacion.nominaoridnaria")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Eliminacion nómina Oridnaria")
    @Post(value = "/eliminacion/nomina/ordinaria", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminar(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaOrdinariaLibService.eliminacionNominaOridnaria(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaordinaria.lista.empleadototalpagonetofiltrar.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.lista.empleadototalpagonetofiltrar.descripcion}",
            operationId = "nominaordinaria.lista.empleadototalpagonetofiltrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Lista empleado total pago neto filtrar")
    @Post(value = "/lista/empleado/total/pago/neto/filtrar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoTotalPagoNetoFiltrado(@Body NominaFiltrado nominaFiltrado){
        try {
            return HttpResponse.ok(nominaOrdinariaLibService.listaEmpleadoTotalPagoNetoFiltrado(nominaFiltrado));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaordinaria.lista.empleadocalculopercepcionesdeduccionesfiltrar.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.lista.empleadocalculopercepcionesdeduccionesfiltrar.descripcion}",
            operationId = "nominaordinaria.lista.empleadocalculopercepcionesdeduccionesfiltrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Lista empleado calculo percepciones deducciones filtrar")
    @Post(value = "/lista/empleado/calculo/percepciones/deducciones/filtrar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoCalculoPercepcionesDeduccionesFiltrar(@Body NominaFiltrado nominaFiltrado){
        try {
            return HttpResponse.ok(nominaOrdinariaLibService.listaEmpleadoCalculoPercepcionesDeduccionesFiltrar(nominaFiltrado));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaordinaria.lista.empleadototalpagonetofechatimbradofiltrar.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.lista.empleadototalpagonetofechatimbradofiltrar.descripcion}",
            operationId = "nominaordinaria.lista.empleadototalpagonetofechatimbradofiltrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Listado empleado total pago neto fecha timbrado filtrar")
    @Post(value = "/listado/empleado/total/pago/neto/fecha/timbrado/filtrar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listadoEmpleadoTotalPagoNetoFechaTimbradoFiltrar(@Body NominaFiltrado nominaFiltrado){
        try {
            return HttpResponse.ok(nominaOrdinariaLibService.listadoEmpleadoTotalPagoNetoFechaTimbradoFiltrar(nominaFiltrado));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaordinaria.recalcula.nomina.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.recalcula.nomina.descripcion}",
            operationId = "nominaordinaria.recalcula.nomina")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Recalcula nomina")
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

    @Operation(summary = "${cosmonaut.controller.nominaordinaria.lista.empleadoscalulados.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.lista.empleadoscalulados.descripcion}",
            operationId = "nominaordinaria.lista.empleadoscalulados")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Recalcula nomina")
    @Get(value = "/lista/empleados/calulados/{nominaPeriodoId}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listadoEmpleadosCalculados(@PathVariable Integer nominaPeriodoId){
        try {
            return HttpResponse.ok( nominaOrdinariaLibService.listadoEmpleadosCalculados(nominaPeriodoId));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaordinaria.lista.empleadocalculopercepcionesdeduccionespaginado.resumen}",
            description = "${cosmonaut.controller.nominaordinaria.lista.empleadocalculopercepcionesdeduccionespaginado.descripcion}",
            operationId = "nominaordinaria.lista.empleadocalculopercepcionesdeduccionespaginado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Ordinaria - Lista empleado calculo percepciones deducciones")
    @Post(value = "/lista/empleado/calculo/percepciones/deducciones/paginado/{numeroRegistros}/{pagina}",
            consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadoCalculoPercepcionesDeduccionesPaginado(@Body Nomina nomina,
                                                                                       @PathVariable Integer numeroRegistros,
                                                                                       @PathVariable Integer pagina){
        try {
            return HttpResponse.ok(nominaOrdinariaLibService.listaEmpleadoCalculoPercepcionesDeduccionesPaginado(nomina,numeroRegistros,pagina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Post(value = "/recalculo/pruebas/{id}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> recalculoPruebas(@PathVariable Integer id){
        try {
            return HttpResponse.ok(calculoNominaOrdinariaServices.reCalculoNomina(id));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
