package mx.com.ga.cosmonaut.nomina.dto;

import lombok.Data;

@Data
public class DetalleDispersion {

    private Double monto;
    private Integer institucionContraparte;
    private Integer referenciaNumerica;
    private String nombreBeneficiario;
    private String conceptoPago;
    private String cuentaBeneficiario;
    private Integer tipoCuentaBeneficiario;
    private Integer tipoPago;
    private String cuentaOrdenante;
    private Integer tipoCuentaOrdenante;
    private String rfcCurpBeneficiario;
    private Integer institucionOperante;

}
