package mx.com.ga.cosmonaut.nomina.dto.dispercion.respuesta;

import lombok.Data;

@Data
public class Timbres {

    private String proveedor;
    private String fecha_expiracion;
    private String timbres_disponibles;
    private String timbres_usados;
    private Metadata metadata;
}
