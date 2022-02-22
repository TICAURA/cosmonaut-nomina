package mx.com.ga.cosmonaut.nomina.service.impl;

import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.nativo.TimbradoRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.nomina.service.PercepcionService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PercepcionServiceImpl implements PercepcionService {

    @Inject
    private TimbradoRepository timbradoRepository;

    @Override
    public RespuestaGenerica listarEmpleado(Integer nominaId, Integer personaId) throws ServiceException {
        try {
            return  new RespuestaGenerica(timbradoRepository.consultaPercepcion(nominaId, personaId),
                    Constantes.RESULTADO_EXITO,Constantes.EXITO);
        } catch (ServiceException e) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " listarEmpleado " + Constantes.ERROR_EXCEPCION, e);
        }
    }
}
