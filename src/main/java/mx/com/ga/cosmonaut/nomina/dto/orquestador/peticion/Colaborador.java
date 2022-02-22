package mx.com.ga.cosmonaut.nomina.dto.orquestador.peticion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Colaborador {

    @JsonProperty(value = "fecha_contrato")
    private String fecha_contrato;
    @JsonProperty(value = "persona_id")
    private Integer persona_id;
    @JsonProperty(value = "cliente_id")
    private Integer cliente_id;

}
