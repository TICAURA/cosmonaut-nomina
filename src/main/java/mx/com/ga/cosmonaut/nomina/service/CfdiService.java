package mx.com.ga.cosmonaut.nomina.service;

import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocClienteXproveedor;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.nomina.dto.DispersionDto;
import mx.com.ga.cosmonaut.nomina.dto.TimbradoDto;

import java.util.List;

public interface CfdiService {

    TimbradoDto genera(List<DispersionDto> lista, NclCentrocClienteXproveedor proveedor) throws ServiceException;

}
