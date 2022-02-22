package mx.com.ga.cosmonaut.nomina.dto.orquestador.peticion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NominaSinSueldo {

    private Integer clienteId;
    private Integer usuarioId;
    private String nombreNomina;
    private Integer cuentaBancoId;
    private Integer monedaId;
    @JsonProperty(value = "fecIniPeriodo")
    private String fecIniPeriodo;
    @JsonProperty(value = "fecFinPeriodo")
    private String fecFinPeriodo;
    private List<Colaborador> colaboradores;

}
