package mx.com.ga.cosmonaut.nomina.service;

import io.micronaut.retry.annotation.CircuitBreaker;
import io.micronaut.retry.annotation.Retryable;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.nomina.dto.DispersionDto;

import java.util.List;

public interface TimbradoService {

    RespuestaGenerica timbrado(Integer empresaId, List<DispersionDto> listaDispersionDto) throws ServiceException;

    @CircuitBreaker
    @Retryable
    RespuestaGenerica consultaDisponibles() throws ServiceException;

    RespuestaGenerica procesando(Long nominaPeriodoId, List<Integer> lista) throws ServiceException;

    RespuestaGenerica respuesta(String respuesta) throws ServiceException;

    RespuestaGenerica resumen(Integer nominaPeriodoId, List<Integer> lista) throws ServiceException;
}
