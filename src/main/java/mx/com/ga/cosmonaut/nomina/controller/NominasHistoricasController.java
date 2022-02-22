package mx.com.ga.cosmonaut.nomina.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.util.Utilidades;
import mx.com.ga.cosmonaut.orquestador.dto.NominaEstadoActualFiltrado;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.Nomina;
import mx.com.ga.cosmonaut.orquestador.service.NominasHistoricasLibService;

import javax.inject.Inject;

@Controller("/nominas-historicas")
public class NominasHistoricasController {

    @Inject
    private NominasHistoricasLibService nominasHistoricasLibService;

    @Operation(summary = "${cosmonaut.controller.nominashistoricas.filtrado.resumen}",
            description = "${cosmonaut.controller.nominashistoricas.filtrado.descripcion}",
            operationId = "nominashistoricas.filtrado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Historica - Filtrado")
    @Post(value = "/filtrado", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> filtrado(@Body NominaEstadoActualFiltrado nomina){
        try {
            return HttpResponse.ok(nominasHistoricasLibService.listaNominaEstadoActualFiltrado(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominashistoricas.consulta.resumen}",
            description = "${cosmonaut.controller.nominashistoricas.consulta.descripcion}",
            operationId = "nominashistoricas.consulta")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Historica - Consulta")
    @Post(value = "/consulta/", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> consulta(@Body Nomina nomina){
        try {
            return HttpResponse.ok(nominasHistoricasLibService.listaNominaEstadoActual(nomina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

    @Operation(summary = "${cosmonaut.controller.nominashistoricas.filtradopaginado.resumen}",
            description = "${cosmonaut.controller.nominashistoricas.filtradopaginado.descripcion}",
            operationId = "nominashistoricas.filtradopaginado")
    @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON),responseCode = "200", description = "Respuesta correcta")
    @Tag(name = "Nómina Historica - Filtrado")
    @Post(value = "/filtrado/paginado/{numeroRegistros}/{pagina}", consumes = MediaType.APPLICATION_JSON, processes = MediaType.APPLICATION_JSON)
    public HttpResponse<RespuestaGenerica> filtradoPaginado(@Body NominaEstadoActualFiltrado nomina,
                                                    @PathVariable Integer numeroRegistros,
                                                    @PathVariable Integer pagina){
        try {
            return HttpResponse.ok(nominasHistoricasLibService.listaNominaEstadoActualFiltrado(nomina,numeroRegistros,pagina));
        }catch (Exception e){
            return HttpResponse.badRequest(Utilidades.respuestaError());
        }
    }

}
