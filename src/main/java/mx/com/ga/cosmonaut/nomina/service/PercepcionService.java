package mx.com.ga.cosmonaut.nomina.service;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface PercepcionService {

    RespuestaGenerica listarEmpleado(Integer nominaId, Integer personaId) throws ServiceException;

}
