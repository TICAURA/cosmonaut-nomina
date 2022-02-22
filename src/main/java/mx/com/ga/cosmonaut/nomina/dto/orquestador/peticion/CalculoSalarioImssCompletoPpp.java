package mx.com.ga.cosmonaut.nomina.dto.orquestador.peticion;

import lombok.Data;

@Data
public class CalculoSalarioImssCompletoPpp {

    private Integer clienteId;
    private Integer politicaId;
    private Integer grupoNomina;
    private Integer tipoCompensacion;
    private Double pagoNeto;
    private Double sdImss;
    private String fechaAntiguedad;
    private String fechaContrato;

}
