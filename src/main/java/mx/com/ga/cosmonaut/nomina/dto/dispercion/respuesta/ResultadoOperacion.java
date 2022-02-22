package mx.com.ga.cosmonaut.nomina.dto.dispercion.respuesta;

import lombok.Data;

@Data
public class ResultadoOperacion {

    private boolean exito;
    private String mensaje;
    private Object contenido;
    private Object resultado_servicio;
    private String codigo_resultado;
    private String tipo;

}
