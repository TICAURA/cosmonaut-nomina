package mx.com.ga.cosmonaut.nomina.dto.dispercion.respuesta;

import lombok.Data;

@Data
public class Resultado {

    private String descripcionError;
    private Long id;
    private String claveRastreo;
    private Boolean exito;
    private String id_operacion;

}
