package mx.com.ga.cosmonaut.nomina.dto.dispercion.respuesta;

import lombok.Data;

@Data
public class ResultadoSTP {

    private String causaDevolucion;
    private String empresa;
    private Integer estado;
    private String folioOrigen;
    private Long id;

}
