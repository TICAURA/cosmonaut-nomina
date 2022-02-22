package mx.com.ga.cosmonaut.nomina.dto.orquestador.peticion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Nomina {

    @JsonProperty(value = "nominaXperiodoId")
    private Integer nominaXperiodoId;
    private String fechaContrato;
    private Integer personaId;
    private Integer clienteId;
    private Integer usuarioId;

}
