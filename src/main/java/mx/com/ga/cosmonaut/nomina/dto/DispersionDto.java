package mx.com.ga.cosmonaut.nomina.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class DispersionDto {

    private Integer centroClienteId;
    private Integer nominaPeriodoId;
    private Integer personaId;
    private Integer usuarioId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", locale = "UTC", timezone = "UTC")
    private Date fechaContrato;

}
