package mx.com.ga.cosmonaut.nomina.service;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;

public interface ConcluirService {

    RespuestaGenerica obtenerConcluir(Integer nominaPeriodoId, Integer centroClienteId) throws ServiceException;

    RespuestaGenerica concluirNomina(Integer nominaPeriodoId) throws ServiceException;

}
