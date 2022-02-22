package mx.com.ga.cosmonaut.nomina.dto.orquestador.peticion;

import lombok.Data;

@Data
public class NominaOrdinaria {

    private Integer clienteId;
    private Integer grupoNomina;
    private Integer usuarioId;
    private String fechaIniPeriodo;
    private String fechaFinPeriodo;
    private String fechaIniIncidencia;
    private String fechaFinIncidencia;
    private String nombreNomina;

}
