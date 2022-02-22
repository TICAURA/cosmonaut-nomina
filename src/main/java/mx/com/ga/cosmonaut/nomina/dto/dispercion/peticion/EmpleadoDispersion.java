package mx.com.ga.cosmonaut.nomina.dto.dispercion.peticion;

import lombok.Data;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrNominaXperiodo;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoPersona;

import java.util.Date;

@Data
public class EmpleadoDispersion {

    private NcrNominaXperiodo nominaXperiodoId;
    private NcoPersona personaId;
    private Date fechaContrato;
    private NclCentrocCliente clienteId;

}
