package mx.com.ga.cosmonaut.nomina.service;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.nomina.dto.DispersionDto;
import mx.com.ga.cosmonaut.nomina.dto.dispercion.peticion.EmpleadoDispersion;
import mx.com.ga.cosmonaut.nomina.dto.dispercion.peticion.RfcPeticion;

import java.util.List;

public interface DispersionServices {

    RespuestaGenerica respuestaBANPAY(String respuesta) throws ServiceException;

    RespuestaGenerica respuestaSTP(String respuesta) throws ServiceException;

    RespuestaGenerica respuestaComplementariaSTP(String respuesta) throws ServiceException;

    RespuestaGenerica dispersion(Integer nominaPeriodoId,List<DispersionDto> listaDispersionDto) throws ServiceException;

    RespuestaGenerica procesando(Integer nominaPeriodoId, List<Long> lista) throws ServiceException;

    RespuestaGenerica obtenerRfc(RfcPeticion rfcPeticion) throws ServiceException;

    RespuestaGenerica resumen(Integer nominaPeriodoId, List<Long> lista) throws ServiceException;

    RespuestaGenerica modificarEstatusEmpleadoDispersion(List<EmpleadoDispersion> empleadoDispersion) throws ServiceException;

}
