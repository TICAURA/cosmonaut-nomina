package mx.com.ga.cosmonaut.nomina.dto.dispercion.respuesta;

import lombok.Data;

@Data
public class ResultadoServicio {

    private String status_servicio;
    private String mensaje_servicio;
    private String identificador_operacion;
    private String identificador_entrada;
    private String servicio;

}
