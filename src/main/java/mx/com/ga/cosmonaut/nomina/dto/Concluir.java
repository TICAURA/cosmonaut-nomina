package mx.com.ga.cosmonaut.nomina.dto;

import lombok.Data;

@Data
public class Concluir {

    private Double totalPagoNeto;
    private Double totalPagoNetoTotal;
    private Double totalPagoNetoDiferencia;
    private Long pagosRealizados;
    private Long pagosRealizadosTotal;
    private Long pagosRealizadosDiferencia;
    private Long recibosPagos;
    private Long recibosPagosTotal;
    private Long recibosPagosDiferencia;

}
