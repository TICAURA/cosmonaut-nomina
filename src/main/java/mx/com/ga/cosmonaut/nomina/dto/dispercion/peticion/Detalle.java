package mx.com.ga.cosmonaut.nomina.dto.dispercion.peticion;

import lombok.Data;

@Data
public class Detalle {

    private String concepto_pago;
    private String cuenta_ordenante;
    private String cuenta_beneficiario;
    private String nombre_beneficiario;
    private String nombre_ordenante;
    private String rfc_curp_beneficiario;
    private String rfc_curp_ordenante;
    private String clave_rastreo;
    private Integer institucion_contraparte;
    private Integer institucion_operante;
    private Integer referencia_numerica;
    private Integer tipo_cuenta_beneficiario;
    private Integer tipo_cuenta_ordenante;
    private Integer tipo_pago;
    private Double monto;

}
