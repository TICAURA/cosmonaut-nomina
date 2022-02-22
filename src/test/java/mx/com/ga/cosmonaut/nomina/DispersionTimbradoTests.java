package mx.com.ga.cosmonaut.nomina;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import mx.com.ga.cosmonaut.common.dto.cms.RespuestaEstatus;
import mx.com.ga.cosmonaut.common.dto.cms.RespuestaGestionContenido;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.util.Cliente;
import mx.com.ga.cosmonaut.nomina.dto.dispercion.peticion.Detalle;
import mx.com.ga.cosmonaut.nomina.dto.dispercion.peticion.Dispersion;
import mx.com.ga.cosmonaut.nomina.dto.dispercion.respuesta.ResultadoOperacion;
import mx.com.ga.cosmonaut.nomina.dto.orquestador.respuesta.NominaRespuesta;
import okhttp3.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@MicronautTest
public class DispersionTimbradoTests {


    @Test
    void testCFDI() {
        try{

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String FechaFinalPago = "2019-09-29";
            String FechaAntiguedad = "2006-07-31";

            Calendar calFechaFinalPago = Calendar.getInstance();
            calFechaFinalPago.setTime(sdf.parse(FechaFinalPago));

            Calendar calFechaAntiguedad = Calendar.getInstance();
            calFechaAntiguedad.setTime(sdf.parse(FechaAntiguedad));

            Date dateFechaFinalPago = sdf.parse(FechaFinalPago);
            Date dateFechaAntiguedad = sdf.parse(FechaAntiguedad);

            int dias = (int) ((dateFechaFinalPago.getTime() - dateFechaAntiguedad.getTime())/86400000);
            String ant = "P" + dias + "D";

        }catch (Exception e){
            e.getStackTrace();
        }
    }

    @Test
    void testClienteDispersion() throws IOException, ServiceException {
        Detalle detalle = new Detalle();
        Dispersion dispersion = new Dispersion();
        Detalle[] detalles = new Detalle[1];

        detalle.setReferencia_numerica(1234567);
        detalle.setNombre_ordenante("LAYHNER");
        detalle.setInstitucion_operante(90646);
        detalle.setCuenta_ordenante("653180003810033227");
        detalle.setRfc_curp_ordenante("XAXX010101000");
        detalle.setMonto(0.01);
        detalle.setTipo_pago(1);
        detalle.setRfc_curp_beneficiario("XAXX010101000");
        detalle.setInstitucion_contraparte(846);
        detalle.setConcepto_pago("Pago de prueba");
        detalle.setNombre_beneficiario("Eduardo");
        detalle.setCuenta_beneficiario("846180000400000001");
        detalle.setTipo_cuenta_ordenante(40);
        detalle.setTipo_cuenta_beneficiario(40);
        detalles[0] = detalle;

        dispersion.setServicio("dispersion_bp");
        dispersion.setUrl_callback("https://cosmonautdev.wintermute.services/nomina/dispersion/respuesta/banpay");
        dispersion.setDetalle(detalles);

        OkHttpClient cliente = new OkHttpClient();
        JSONObject json = new JSONObject(dispersion);
        RequestBody cuerpoSolicitud = RequestBody.create(json.toString(),null);
        Request solicitud = new Request.Builder()
                .url("https://us-central1-cosmonaut-299500.cloudfunctions.net/cosmonaut-dispersion-async")
                .put(cuerpoSolicitud)
                .addHeader("Content-Type","application/json")
                .build();
        Call llamada = cliente.newCall(solicitud);
        Response respuesta = llamada.execute();
        System.out.println(respuesta.body().string());
        System.out.println(respuesta.message());

        if (respuesta.isSuccessful()){
            ObjectMapper objectMapper = new ObjectMapper();
            //return objectMapper.readValue(Objects.requireNonNull(respuesta.body()).string(), ResultadoOperacion.class);
        }
    }

    @Test
    void testClienteDispersionRespuesta() throws IOException, ServiceException {
        String respuesta = "{\"codigo_resultado\":\"000\",\"contenido\":[{\"disp\":\"Entrada[0]  ORD 653180003810033227 BEN 846180000400000001\",\"id_operacion\":\"f29aa2cf-ee71-4cda-ae92-35c73177b235\"}],\"exito\":true,\"mensaje\":\"Dispersiones solicitadass\",\"resultado_servicio\":[],\"tipo\":\"dispersion\"}\n";
        ObjectMapper objectMapper = new ObjectMapper();
        ResultadoOperacion resultadoOperacion = objectMapper.readValue(respuesta, ResultadoOperacion.class);
        System.out.println(resultadoOperacion.getCodigo_resultado());
    }

}
