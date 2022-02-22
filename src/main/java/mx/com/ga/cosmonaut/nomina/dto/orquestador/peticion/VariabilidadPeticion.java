package mx.com.ga.cosmonaut.nomina.dto.orquestador.peticion;

import lombok.Data;

@Data
public class VariabilidadPeticion {

    private Integer variabilidad;
    private Integer clienteId;
    private Integer bimestre;
    private Integer anioFiscal;
    private Integer usuarioId;
    private String fechaAplicacion;

}
