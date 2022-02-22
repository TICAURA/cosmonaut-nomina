package mx.com.ga.cosmonaut.nomina.dto.dispercion.peticion;

import lombok.Data;

@Data
public class Dispersion {

    private String servicio;
    private String csd_id;
    private String id_operacion;
    private String[] cfdi;
    private String url_callback;
    private Detalle[] detalle;
    private Boolean firma;

}
