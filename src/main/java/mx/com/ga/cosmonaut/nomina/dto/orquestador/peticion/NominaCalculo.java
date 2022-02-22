package mx.com.ga.cosmonaut.nomina.dto.orquestador.peticion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class NominaCalculo {

    private Integer clienteId;
    private Integer politicaId;
    private Integer grupoNomina;
    private Integer tipoCompensacion;
    @JsonProperty(value = "sbmImss")
    private BigDecimal sbmImss;
    private String fechaAntiguedad;
    private String fecIniPeriodo;

}
