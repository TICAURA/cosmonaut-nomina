package mx.com.ga.cosmonaut.nomina.dto.orquestador.respuesta;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NominaRespuesta {

    @JsonProperty(value = "response")
    private Object response;
    @JsonProperty(value = "responseDetail")
    private DetalleRespuesta responseDetail;

}
