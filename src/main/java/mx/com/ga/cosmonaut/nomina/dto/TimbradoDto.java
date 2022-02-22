package mx.com.ga.cosmonaut.nomina.dto;

import lombok.Data;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrBitacoraTimbrado;

import java.util.List;

@Data
public class TimbradoDto {

    private String[] xmls;
    List<NcrBitacoraTimbrado> listaBitacora;
}
