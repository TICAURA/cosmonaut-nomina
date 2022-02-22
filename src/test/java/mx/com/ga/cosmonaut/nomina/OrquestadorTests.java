package mx.com.ga.cosmonaut.nomina;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.sse.Event;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.nomina.dto.orquestador.peticion.Nomina;
import mx.com.ga.cosmonaut.nomina.dto.orquestador.peticion.NominaCalculo;
import mx.com.ga.cosmonaut.orquestador.dto.peticion.LoginRequest;
import mx.com.ga.cosmonaut.orquestador.dto.respuesta.LoginResponse;
import mx.com.ga.cosmonaut.orquestador.service.CalculaNominaLiquidacionService;
import mx.com.ga.cosmonaut.orquestador.service.CalculaNominaPtuService;
import mx.com.ga.cosmonaut.orquestador.service.CalculoNominaAguinaldoService;
import mx.com.ga.cosmonaut.orquestador.service.CalculoNominaOrdinariaServices;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import mx.com.ga.cosmonaut.orquestador.dto.respuesta.RespuestaAsyncNomina;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrquestadorTests {

    private static final Logger LOG = LoggerFactory.getLogger(OrquestadorTests.class);

    @Inject
    @Client("/nomina-ordinaria")
    private RxHttpClient cliente;

    @Inject
    @Client("https://cosmonautdev.wintermute.services/admin")
    private RxHttpClient clienteAdmin;

    @Inject
    @Client("/calculo")
    private RxHttpClient clienteCalculo;

    @Inject
    private CalculoNominaOrdinariaServices calculoNominaOrdinariaServices;

    @Inject
    private CalculoNominaAguinaldoService calculoNominaAguinaldoService;

    @Inject
    private CalculaNominaPtuService calculaNominaPtuService;

    @Inject
    private CalculaNominaLiquidacionService calculaNominaLiquidacionService;

    private String token;

    @Test
    @Order(0)
    void testLogin() {
        LoginRequest request = new LoginRequest();
        request.setUsername("naye291189+rh5@gmail.com");
        request.setPassword("P@ssword2");
        final LoginResponse respuesta = clienteAdmin.toBlocking().retrieve(HttpRequest.POST("/login",request), LoginResponse.class);
        token = respuesta.getAccess_token();
    }

    @Test
    @Order(1)
    void testCalculoSalarioBrutoMensual() {
        NominaCalculo nominaCalculo = new NominaCalculo();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String fechaInicioPeriodo = df.format(new Date());
        nominaCalculo.setClienteId(464);
        nominaCalculo.setPoliticaId(248);
        nominaCalculo.setGrupoNomina(230);
        nominaCalculo.setTipoCompensacion(1);
        nominaCalculo.setSbmImss(BigDecimal.valueOf(20000));
        nominaCalculo.setFechaAntiguedad("2001-02-28");
        nominaCalculo.setFecIniPeriodo(fechaInicioPeriodo);

        final RespuestaGenerica respuesta =
                clienteCalculo.toBlocking().retrieve(HttpRequest.POST("/salario/bruto/mensual/",nominaCalculo).bearerAuth(token),
                        RespuestaGenerica.class);
        LOG.info("Respuesta {}", respuesta.getDatos());
        assertTrue(respuesta.isResultado());
    }

    @Test
    @Order(2)
    void testConsultaNominasActivas() {
        Nomina nomina = new Nomina();
        nomina.setClienteId(464);
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/consulta/nominas/activas/",nomina),
                        RespuestaGenerica.class);
        LOG.info("Respuesta {}", respuesta.getDatos());
        assertTrue(respuesta.isResultado());
    }

    @Test
    @Order(3)
    void testCalculaNominaPeriodo() {
        Nomina nomina = new Nomina();
        nomina.setClienteId(464);
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/consulta/nominas/activas/",nomina),
                        RespuestaGenerica.class);
        LOG.info("Respuesta {}", respuesta.getDatos());
        assertTrue(respuesta.isResultado());
    }

    @Test
    @Order(4)
    void testRevisarProcesoNomina() {
        Nomina nomina = new Nomina();
        nomina.setNominaXperiodoId(1052);
        final RespuestaGenerica respuesta =
                cliente.toBlocking().retrieve(HttpRequest.POST("/revisar/proceso/nomina",nomina),
                        RespuestaGenerica.class);
        LOG.info("Respuesta {}", respuesta.getDatos());
        assertTrue(respuesta.isResultado());
    }

    @Test
    @Order(5)
    void testCalculaOrdinaria() throws ServiceException {
        RespuestaGenerica respuesta = calculoNominaOrdinariaServices.calculoNominaOrdinaria(1599);
        LOG.info("Respuesta {}", respuesta.getDatos());
        assertTrue(respuesta.isResultado());
    }



    @Test
    void testCalculaOrdinariaAsync() throws ServiceException {
        PublishSubject<Event<RespuestaAsyncNomina>> publisher = PublishSubject.create();
        Flowable<Event<RespuestaAsyncNomina>> flowable = publisher.toFlowable(BackpressureStrategy.BUFFER);
        calculoNominaOrdinariaServices.calculoNominaOrdinariaAsync(1636, publisher);
        Iterable<Event<RespuestaAsyncNomina>> iterable = flowable.blockingIterable();
        iterable.forEach( t -> assertNull(t.getData().getException()));
    }

    @Test
    @Order(6)
    void testRecalculaOrdinaria() throws ServiceException {
        RespuestaGenerica respuesta = calculoNominaOrdinariaServices.reCalculoNomina(1636);
        LOG.info("Respuesta {}", respuesta.getDatos());
        assertTrue(respuesta.isResultado());
    }

    @Test
    @Order(7)
    void testRecalculaOrdinariaAsync() throws ServiceException, InterruptedException {
        AtomicBoolean completed = new AtomicBoolean(false);
        PublishSubject<Event<RespuestaAsyncNomina>> publisher = PublishSubject.create();
        Flowable<Event<RespuestaAsyncNomina>> flowable = publisher.toFlowable(BackpressureStrategy.BUFFER);
        flowable.doOnComplete(() -> {
            completed.set(true);
        });
        calculoNominaOrdinariaServices.reCalculoNominaAsync(1599, publisher);
        while(!completed.get()){
            Thread.sleep(500);
        }
        Iterable<Event<RespuestaAsyncNomina>> iterable = flowable.blockingIterable();
        iterable.forEach( t -> assertNull(t.getData().getException()));
    }


    @Test
    void testCalculaAguinaldo() throws ServiceException {
        RespuestaGenerica respuesta = calculoNominaAguinaldoService.calcularNomina(1670);
        LOG.info("Respuesta {}", respuesta.getDatos());
        assertTrue(respuesta.isResultado());
    }

    @Test
    @Order(8)
    void testReCalculaAguinaldo() throws ServiceException {
        RespuestaGenerica respuesta = calculoNominaAguinaldoService.reCalculoNomina(1547);
        LOG.info("Respuesta {}", respuesta.getDatos());
        assertTrue(respuesta.isResultado());
    }

    @Test
    @Order(9)
    void testCalculaNominaPtu() throws ServiceException {
        RespuestaGenerica respuesta = calculaNominaPtuService.calcularNomina(1444);
        LOG.info("Respuesta {}", respuesta.getDatos());
        assertTrue(respuesta.isResultado());
    }

    @Test
    @Order(10)
    void testReCalculaNominaPtu() throws ServiceException {
        RespuestaGenerica respuesta = calculaNominaPtuService.reCalculoNomina(1444);
        LOG.info("Respuesta {}", respuesta.getDatos());
        assertTrue(respuesta.isResultado());
    }

    @Test
    @Order(11)
    void testCalculaNominaLiquidacion() throws ServiceException {
        RespuestaGenerica respuesta = calculaNominaLiquidacionService.calculoNomina(1661);
        LOG.info("Respuesta {}", respuesta.getDatos());
        assertTrue(respuesta.isResultado());
    }

    @Test
    @Order(12)
    void testReCalculaNominaLiquidacion() throws ServiceException {
        RespuestaGenerica respuesta = calculaNominaLiquidacionService.reCalculoNomina(1661);
        LOG.info("Respuesta {}", respuesta.getDatos());
        assertTrue(respuesta.isResultado());
    }

    @Test
    @Order(13)
    void testAnioAntiguedad() throws ServiceException, ParseException {
        Calendar fechaDesde = Calendar.getInstance();
        Calendar fechaHasta = Calendar.getInstance();
        Date fechaInicio = new SimpleDateFormat("dd/MM/yyyy").parse("01/08/2020");
        Date fechaFin = new SimpleDateFormat("dd/MM/yyyy").parse("20/07/2021");

        fechaDesde.setTime(fechaInicio);
        fechaHasta.setTime(fechaFin);
        int incremento = 0;

        if (fechaDesde.get(Calendar.DAY_OF_MONTH) > fechaHasta.get(Calendar.DAY_OF_MONTH)) {
            incremento = fechaDesde.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        if (incremento != 0) {
            incremento = 1;
        }

        if ((fechaDesde.get(Calendar.MONTH) + incremento) > fechaHasta.get(Calendar.MONTH)) {
            incremento = 1;
        } else {
            incremento = 0;
        }

        Integer respuesta =  fechaHasta.get(Calendar.YEAR) - (fechaDesde.get(Calendar.YEAR) + incremento);
        LOG.info("Respuesta {}", respuesta);
    }


}

