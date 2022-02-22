package mx.com.ga.cosmonaut.nomina.service.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrBitacoraPago;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrBitacoraTimbrado;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrBitacoraPagoRepository;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrBitacoraTimbradoRepository;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrEmpleadoXnominaRepository;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrNominaXperiodoRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.nomina.dto.Concluir;
import mx.com.ga.cosmonaut.nomina.service.ConcluirService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class ConcluirServiceImpl implements ConcluirService {

    @Inject
    private NcrBitacoraTimbradoRepository ncrBitacoraTimbradoRepository;

    @Inject
    private NcrBitacoraPagoRepository ncrBitacoraPagoRepository;

    @Inject
    private NcrNominaXperiodoRepository ncrNominaXperiodoRepository;

    @Inject
    private NcrEmpleadoXnominaRepository ncrEmpleadoXnominaRepository;

    @Override
    public RespuestaGenerica obtenerConcluir(Integer nominaPeriodoId, Integer centroClienteId) throws ServiceException {
        try{
            Concluir concluir = new Concluir();
            List<Concluir> lista = new ArrayList<>();

            Long pagos = ncrEmpleadoXnominaRepository.
                    countByCentrocClienteIdCentrocClienteIdAndNominaXperiodoIdNominaXperiodoIdAndEstadoPagoId(
                            centroClienteId,nominaPeriodoId,5);

            Long timbrado = ncrEmpleadoXnominaRepository.
                    countByCentrocClienteIdCentrocClienteIdAndNominaXperiodoIdNominaXperiodoIdAndEstadoTimbreId(
                            centroClienteId,nominaPeriodoId,3);

            Long empleados = ncrEmpleadoXnominaRepository.countByConcluirEmpleados(centroClienteId,nominaPeriodoId);

            Optional<Double> montoEmpleados = ncrEmpleadoXnominaRepository.
                    findSumTotalNetoByCentrocClienteIdCentrocClienteIdAndNominaXperiodoIdNominaXperiodoIdAndEstadoPagoId(
                            centroClienteId,nominaPeriodoId,5);

            Optional<Double> total = ncrEmpleadoXnominaRepository.
                    findSumConcluirTotalNeto(centroClienteId,nominaPeriodoId);

            if(empleados == timbrado){
                ncrNominaXperiodoRepository.updateByNominaXperiodoId(nominaPeriodoId,4);
            }

            concluir.setPagosRealizados(pagos != null ? pagos : 0L);
            concluir.setPagosRealizadosTotal(empleados != null ? empleados : 0L);
            concluir.setPagosRealizadosDiferencia(empleados != null ? empleados - pagos : 0L);
            concluir.setRecibosPagos(timbrado != null ? timbrado : 0L);
            concluir.setRecibosPagosTotal(empleados != null ? empleados : 0L);
            concluir.setRecibosPagosDiferencia(empleados != null ? empleados- timbrado : 0L);
            concluir.setTotalPagoNeto(total.isPresent() ? total.get() : 0.0);
            concluir.setTotalPagoNetoTotal(montoEmpleados.isPresent() ? montoEmpleados.get() : 0.0);
            concluir.setTotalPagoNetoDiferencia(montoEmpleados.isPresent() ? montoEmpleados.get() - total.get() : total.get());
            lista.add(concluir);
            return new RespuestaGenerica(lista,Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtenerConcluir " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica concluirNomina(Integer nominaPeriodoId) throws ServiceException {
        try{

            long empleadoXnomina = ncrEmpleadoXnominaRepository.countByNominaXperiodoIdNominaXperiodoId(nominaPeriodoId);
            long empleadosTimbrados = ncrEmpleadoXnominaRepository.countByNominaXperiodoIdNominaXperiodoIdAndEstadoTimbreId(nominaPeriodoId,3);

            if (empleadoXnomina == empleadosTimbrados){
                ncrNominaXperiodoRepository.updateByNominaXperiodoId(nominaPeriodoId,5);
                return new RespuestaGenerica(null,Constantes.RESULTADO_EXITO, Constantes.EXITO);
            }else {
                return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR, Constantes.ERROR_CONCLUIR_NOMINA);
            }

        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " concluirNomina " + Constantes.ERROR_EXCEPCION, e);
        }
    }
}
