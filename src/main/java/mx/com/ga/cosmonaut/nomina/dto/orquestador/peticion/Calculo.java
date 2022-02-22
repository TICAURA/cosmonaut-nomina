package mx.com.ga.cosmonaut.nomina.dto.orquestador.peticion;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Calculo {

    private Integer clienteId;
    private String periodicidadPagoId;
    private BigDecimal sbmImss;
    private boolean inluyeImms;
    private boolean inlcuyeSubsidio;
    private Integer diasPeriodo;

}
