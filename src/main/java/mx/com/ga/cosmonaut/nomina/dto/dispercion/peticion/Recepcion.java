package mx.com.ga.cosmonaut.nomina.dto.dispercion.peticion;

import lombok.Data;

@Data
public class Recepcion {

    private String id;
    private String empresa;
    private Integer folioOrigen;
    private String estado;
    private String causaDevolucion;

}
