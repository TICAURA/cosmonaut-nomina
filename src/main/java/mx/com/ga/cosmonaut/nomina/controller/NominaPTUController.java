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
import mx.com.ga.cosmonaut.orquestador.dto.peticion.NominaFiltrado;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.NominaSinSueldo;
import mx.com.ga.cosmonaut.orquestador.service.NominaPTULibServices;
import mx.com.ga.cosmonaut.orquestador.service.NominaService;

import javax.inject.Inject;

@Controller("/nomina-ptu")
public class NominaPTUController {

    @Inject
    private NominaPTULibServices nominaPTULibServices;

    @Inject
    private NominaService nominaService;

    @Operation(summary = "${cosmonaut.controller.nominaptu.listado.nominasptu.resumen}",
            description = "${cosmonaut.controller.nominaptu.listado.nominasptu.descripcion}",
            operationId = "nominaptu.listado.nominasptu")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Listado de nóminas PTU")
    @Post(value = "/listado/nominas/ptu", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listadoNominasPTU(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaPTULibServices.listadoNominasPTU(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaptu.calculo.nominaptu.resumen}",
            description = "${cosmonaut.controller.nominaptu.calculo.nominaptu.descripcion}",
            operationId = "nominaptu.calculo.nominaptu")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Cólculo de nómina PTU")
    @Post(value = "/calculo/nomina/ptu", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> calcular(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaService.calcular(nomina.getNominaXperiodoId()));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaptu.lista.empleadosptu.resumen}",
            description = "${cosmonaut.controller.nominaptu.lista.empleadosptu.descripcion}",
            operationId = "nominaptu.lista.empleadosptu")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Lista de empleados PTU")
    @Post(value = "/lista/empleados/ptu", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadosPTU(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaPTULibServices.listaEmpleadosPTU(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaptu.detalle.empleadonominaptu.resumen}",
            description = "${cosmonaut.controller.nominaptu.detalle.empleadonominaptu.descripcion}",
            operationId = "nominaptu.detalle.empleadonominaptu")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Detalle por empleado nómina PTU")
    @Post(value = "/detalle/empleado/nomina/ptu", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> detalleEmpleadoNominaPTU(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaPTULibServices.detalleEmpleadoNominaPTU(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaptu.listado.empleadostotalpagonetoptu.resumen}",
            description = "${cosmonaut.controller.nominaptu.listado.empleadostotalpagonetoptu.descripcion}",
            operationId = "nominaptu.listado.empleadostotalpagonetoptu")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Listado de empleados con total de pago neto de PTU")
    @Post(value = "/listado/empleados/total-pago-neto/ptu", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listadoEmpleadosTotalPagoNetoPTU(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaPTULibServices.listadoEmpleadosTotalPagoNetoPTU(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
/**
    @Operation(summary = "${cosmonaut.controller.nominaptu.descarga.dispersionptu.resumen}",
            description = "${cosmonaut.controller.nominaptu.descarga.dispersionptu.descripcion}",
            operationId = "nominaptu.descarga.dispersionptu")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Descarga de dispersión de PTU")
    @Post(value = "/descarga/dispersion/ptu", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> descargaDispersionPTU(@Body Nomina nomina){
        try {
            return HttpResponse.ok(OrquestadorUtil.generaRespuesta(nominaPTUServices.descargaDispersionPTU(
                    nomina, OrquestadorConstantes.ORQUESTADOR_CVE_67)));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
*/
    @Operation(summary = "${cosmonaut.controller.nominaptu.listado.empleadosotalpagonetoptutimbrado.resumen}",
            description = "${cosmonaut.controller.nominaptu.listado.empleadosotalpagonetoptutimbrado.descripcion}",
            operationId = "nominaptu.listado.empleadosotalpagonetoptutimbrado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Listado de empleados con total de pago neto de PTU y timbrado")
    @Post(value = "/listado/empleados/total-pago-neto/ptu/timbrado", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listadoEmpleadosTotalPagoNetoPtuTimbrado(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaPTULibServices.listadoEmpleadosTotalPagoNetoPtuTimbrado(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaptu.detalle.empleadostotalpagonetodetallemontostimbradoptu.resumen}",
            description = "${cosmonaut.controller.nominaptu.detalle.empleadostotalpagonetodetallemontostimbradoptu.descripcion}",
            operationId = "nominaptu.detalle.empleadostotalpagonetodetallemontostimbradoptu")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Detalle de empleados con total de pago neto y detalle de montos a timbrado de PTU")
    @Post(value = "/detalle/empleados/total/pago-neto/detalle/montos/timbrado/ptu", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> detalleEmpleadosTotalPagoNetoDetalleMontosTimbradoPTU(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaPTULibServices.detalleEmpleadosTotalPagoNetoDetalleMontosTimbradoPTU(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
/**
    @Operation(summary = "${cosmonaut.controller.nominaptu.recibos.ptu.resumen}",
            description = "${cosmonaut.controller.nominaptu.recibos.ptu.descripcion}",
            operationId = "nominaptu.recibos.ptu")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Recibos PTU")
    @Post(value = "/recibos/ptu", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> recibosPTU(@Body Nomina nomina){
        try {
            return HttpResponse.ok(OrquestadorUtil.generaRespuesta(nominaPTUServices.recibosPTU(
                    nomina, OrquestadorConstantes.ORQUESTADOR_CVE_71)));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
*/

    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaptu.guardar.nominaptu.resumen}",
            description = "${cosmonaut.controller.nominaptu.guardar.nominaptu.descripcion}",
            operationId = "nominaptu.guardar.nominaptu")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Crear nómina PTU")
    @Post(value = "/guardar/nomina/ptu", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> crear(@Header("datos-flujo") String datosFlujo,
                                                 @Header("datos-sesion") String datosSesion,
                                                 @Body NominaSinSueldo nominaSinSueldo){
        try {
            return HttpResponse.ok(nominaPTULibServices.guardarNominaPTU(nominaSinSueldo));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaptu.eliminacion.nominaptu.resumen}",
            description = "${cosmonaut.controller.nominaptu.eliminacion.nominaptu.descripcion}",
            operationId = "nominaptu.eliminacion.nominaptu")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Eliminacion Nomina PTU")
    @Post(value = "/eliminacion/nomina/ptu", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> eliminar(@Header("datos-flujo") String datosFlujo,
                                                    @Header("datos-sesion") String datosSesion,
                                                    @Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaPTULibServices.eliminacionNominaPTU(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaptu.lista.empleadosptufiltrar.resumen}",
            description = "${cosmonaut.controller.nominaptu.lista.empleadosptufiltrar.descripcion}",
            operationId = "nominaptu.lista.empleadosptufiltrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Lista de empleados PTU filtrar")
    @Post(value = "/lista/empleados/ptu/filtrar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadosPTUFiltrar(@Body NominaFiltrado nomina){
        try {
            return HttpResponse.ok(nominaPTULibServices.listaEmpleadosPTUFiltrar(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaptu.listado.empleadostotalpagonetoptufiltrar.resumen}",
            description = "${cosmonaut.controller.nominaptu.listado.empleadostotalpagonetoptufiltrar.descripcion}",
            operationId = "nominaptu.listado.empleadostotalpagonetoptufiltrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Listado de empleados con total de pago neto de PTU filtrar")
    @Post(value = "/listado/empleados/total-pago-neto/ptu/filtrar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listadoEmpleadosTotalPagoNetoPTUFiltrar(@Body NominaFiltrado nomina){
        try {
            return HttpResponse.ok(nominaPTULibServices.listadoEmpleadosTotalPagoNetoPTUFiltrar(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaptu.listado.empleadosotalpagonetoptutimbradofiltrar.resumen}",
            description = "${cosmonaut.controller.nominaptu.listado.empleadosotalpagonetoptutimbradofiltrar.descripcion}",
            operationId = "nominaptu.listado.empleadosotalpagonetoptutimbradofiltrar")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Listado de empleados con total de pago neto de PTU y timbrado filtrar")
    @Post(value = "/listado/empleados/total-pago-neto/ptu/timbrado/filtrar", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listadoEmpleadosTotalPagoNetoPtuTimbradoFiltrar(@Body NominaFiltrado nomina){
        try {
            return HttpResponse.ok(nominaPTULibServices.listadoEmpleadosTotalPagoNetoPtuTimbradoFiltrar(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @BitacoraSistema
    @BitacoraNomina
    @Operation(summary = "${cosmonaut.controller.nominaptu.recalculo.nominaptu.resumen}",
            description = "${cosmonaut.controller.nominaptu.recalculo.nominaptu.descripcion}",
            operationId = "nominaptu.recalculo.nominaptu")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Cólculo de nómina PTU")
    @Post(value = "/recalculo/nomina/ptu", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> recalcular(@Header("datos-flujo") String datosFlujo,
                                                      @Header("datos-sesion") String datosSesion,
                                                      @Body Nomina nomina){
        try {
            return HttpResponse.ok(nominaService.calcular(nomina.getNominaXperiodoId()));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominaptu.lista.empleadosptupaginado.resumen}",
            description = "${cosmonaut.controller.nominaptu.lista.empleadosptupaginado.descripcion}",
            operationId = "nominaptu.lista.empleadosptupaginado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina PTU - Lista de empleados PTU")
    @Post(value = "/lista/empleados/ptu/paginado/{numeroRegistros}/{pagina}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> listaEmpleadosPTUPaginado(@Body Nomina nomina,
                                                             @PathVariable Integer numeroRegistros,
                                                             @PathVariable Integer pagina){
        try {
            return HttpResponse.ok(nominaPTULibServices.listaEmpleadosPTUPaginado(nomina,numeroRegistros, pagina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }
}
