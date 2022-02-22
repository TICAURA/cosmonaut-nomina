package mx.com.ga.cosmonaut.nomina.dto.orquestador.respuesta;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DetalleRespuesta {

    @JsonProperty(value = "code")
    private Integer code;
    @JsonProperty(value = "message")
    private String message;

}
