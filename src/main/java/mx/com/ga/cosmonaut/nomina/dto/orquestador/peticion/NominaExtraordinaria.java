package mx.com.ga.cosmonaut.nomina.dto.orquestador.peticion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class NominaExtraordinaria {

    private Integer clienteId;
    private Integer usuarioId;
    private String nombreNomina;
    private Integer cuentaBancoId;
    private Integer monedaId;
    @JsonProperty(value = "fecXReportes")
    private String fecXReportes;
    private boolean todos;
    private List<Empleados> empleados;

}
