package mx.com.ga.cosmonaut.nomina.dto.dispercion.respuesta;

import lombok.Data;

import java.util.List;

@Data
public class ResultadoOperacionTimbres {

    private boolean exito;
    private String mensaje;
    private List<Timbres> contenido;
    private Object resultado_servicio;
    private String codigo_resultado;
    private String tipo;

}
