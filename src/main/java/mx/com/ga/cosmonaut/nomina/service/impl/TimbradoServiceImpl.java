package mx.com.ga.cosmonaut.nomina.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrBitacoraPago;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrBitacoraTimbrado;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrEmpleadoXnomina;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrTimbre;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocCliente;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocClienteXproveedor;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.calculo.*;
import mx.com.ga.cosmonaut.common.repository.cliente.NclCentrocClienteRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclCentrocClienteXproveedorRepository;
import mx.com.ga.cosmonaut.common.util.Cliente;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.nomina.dto.DispersionDto;
import mx.com.ga.cosmonaut.nomina.dto.TimbradoDto;
import mx.com.ga.cosmonaut.nomina.dto.dispercion.peticion.Dispersion;
import mx.com.ga.cosmonaut.nomina.dto.dispercion.respuesta.*;
import mx.com.ga.cosmonaut.nomina.service.CfdiService;
import mx.com.ga.cosmonaut.nomina.service.TimbradoService;
import mx.com.ga.cosmonaut.orquestador.service.ComprobanteFiscalServices;
import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Singleton
public class TimbradoServiceImpl implements TimbradoService {

    private static final Logger LOG = LoggerFactory.getLogger(TimbradoServiceImpl.class);

    @Inject
    @Client("${servicio.dispersion-timbrado.host}")
    private RxHttpClient cliente;

    @Value("${servicio.dispersion-timbrado.timbrado.path}")
    private String contextoTimbrado;

    @Value("${servicio.dispersion-timbrado.urlCallback.timbrado}")
    private String urlDevolucion;

    @Inject
    private NclCentrocClienteRepository nclCentrocClienteRepository;

    @Inject
    private CfdiService cfdiService;

    @Inject
    private NcrBitacoraTimbradoRepository ncrBitacoraTimbradoRepository;

    @Inject
    private NcrTimbreRepository ncrTimbreRepository;

    @Inject
    private NclCentrocClienteXproveedorRepository nclCentrocClienteXproveedorRepository;

    @Inject
    private NcrNominaXperiodoRepository ncrNominaXperiodoRepository;

    @Inject
    private NcrEmpleadoXnominaRepository empleadoXnominaRepository;

    @Inject
    private ComprobanteFiscalServices comprobanteFiscalServices;

    @Inject
    private NcrBitacoraPagoRepository ncrBitacoraPagoRepository;

    @Override
    public RespuestaGenerica timbrado(Integer empresaId, List<DispersionDto> listaDispersionDto)
            throws ServiceException {
        try{
            ResultadoOperacion resultadoOperacion;
            NclCentrocCliente empresa = nclCentrocClienteRepository.findById(empresaId).
                    orElseThrow(() -> new ServiceException(Constantes.ERROR_CLIENTE_NO_EXISTE));

            Optional<NclCentrocClienteXproveedor> proveedor = nclCentrocClienteXproveedorRepository.
                    findByCentrocClienteIdCentrocClienteId(empresaId);

            if (proveedor.isPresent() && proveedor.get().getProveedorTimbradoId() != null){
                TimbradoDto timbradoDto = cfdiService.genera(listaDispersionDto,proveedor.get());
                if (empresa != null && empresa.getCertificadoSelloDigitalId() != null &&
                        !empresa.getCertificadoSelloDigitalId().isEmpty()){

                    resultadoOperacion = clienteTimbrado(proveedor.get().getProveedorTimbradoId().getDescripcion(),
                            empresa.getCertificadoSelloDigitalId(), timbradoDto.getXmls());

                    if (resultadoOperacion == null){
                        return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR,Constantes.ERROR_TIMBRAR);
                    }

                    this.guardaBitacora(resultadoOperacion.getContenido(), timbradoDto.getListaBitacora());
                    ncrNominaXperiodoRepository.
                            updateByNominaXperiodoId(timbradoDto.getListaBitacora().get(0).getNominaPeriodoId(),7);

                    if (resultadoOperacion.isExito()){
                        return new RespuestaGenerica(resultadoOperacion,Constantes.RESULTADO_EXITO,Constantes.EXITO);
                    }else {
                        return new RespuestaGenerica(resultadoOperacion, Constantes.RESULTADO_ERROR,Constantes.ERROR_TIMBRAR);
                    }

                }else{
                    return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ERROR_EMPRESA_CSD);
                }
            }else {
                return new RespuestaGenerica(null,Constantes.RESULTADO_ERROR,Constantes.ERROR_TIMBRADO_PROVEEDOR);
            }

        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " timbrado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica consultaDisponibles() throws ServiceException {
        try{
            List<ResultadoOperacionTimbres> lista =  new ArrayList<>();
            ResultadoOperacionTimbres resultadoSw = clienteTimbres("facturacion_sw");
            ResultadoOperacionTimbres resultadoFi = clienteTimbres("facturacion_fi");
            lista.add(resultadoSw);
            lista.add(resultadoFi);

            return new RespuestaGenerica(lista, Constantes.RESULTADO_EXITO,Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " consultaTimbresDisponibles " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica procesando(Long nominaPeriodoId,List<Integer> lista) throws ServiceException {
        try{
            Long empleadosTimbrados = ncrBitacoraTimbradoRepository.
                    countByNominaPeriodoIdAndEsActualAndPersonaIdInListAndEstadoTimbreIdNotEquals(nominaPeriodoId.intValue(), true,lista,1);
            Long porcentaje = (100 * empleadosTimbrados) / lista.size();
            return new RespuestaGenerica(porcentaje, Constantes.RESULTADO_EXITO,Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " procesando " + Constantes.ERROR_EXCEPCION, e);
        }
    }
    
    @Override
    public RespuestaGenerica respuesta(String respuesta) throws ServiceException {
        try{
            LOG.info("Se recibe repuesta de timbrado");
            ObjectMapper objectMapper = new ObjectMapper();
            ResultadoOperacion respuestaTimbrado = objectMapper.readValue(respuesta, ResultadoOperacion.class);
            List<Object> map = (List<Object>) respuestaTimbrado.getResultado_servicio();
            ResultadoServicio resultadoServicio = ObjetoMapper.mapAll(map,ResultadoServicio.class).get(0);

            boolean esCorrecto = this.validaRespuestaTimbrado(respuestaTimbrado);

            ncrBitacoraTimbradoRepository.updateByOperacionId(
                    resultadoServicio.getIdentificador_operacion(),
                    resultadoServicio.getMensaje_servicio(),
                    esCorrecto,
                    esCorrecto ? 3 : 2);

            NcrBitacoraTimbrado bitacoraTimbrado =
                    ncrBitacoraTimbradoRepository.findByOperacionId(resultadoServicio.getIdentificador_operacion());

            NcrEmpleadoXnomina empleado = empleadoXnominaRepository.
                    findByNominaXperiodoIdNominaXperiodoIdAndPersonaIdPersonaId(bitacoraTimbrado.getNominaPeriodoId().intValue(),
                            bitacoraTimbrado.getPersonaId().intValue());

            empleadoXnominaRepository.updateByNominaXperiodoIdAndPersonaId(
                    empleado.getNominaXperiodoId(),
                    empleado.getPersonaId(), empleado.getEstadoPagoId(), esCorrecto ? 3 : 2);

            if(esCorrecto){
                LOG.info("La respuesta del timbrado fue exitosa");
                List<Object> mapContenido = (List<Object>) respuestaTimbrado.getContenido();
                ContenidoCFDI contenido = ObjetoMapper.mapAll(mapContenido,ContenidoCFDI.class).get(0);

                Optional<NcrTimbre> timbradoActual =
                        ncrTimbreRepository.findByNominaPeriodoIdAndCentrocClienteIdAndPersonaIdAndEsActual(
                                bitacoraTimbrado.getNominaPeriodoId(), bitacoraTimbrado.getClienteId(),
                                bitacoraTimbrado.getPersonaId(), true);
                if (timbradoActual.isPresent()){
                    timbradoActual.get().setEsActual(false);
                    ncrTimbreRepository.update(timbradoActual.get());
                }

                NcrTimbre timbre = new NcrTimbre();
                timbre.setNominaPeriodoId(bitacoraTimbrado.getNominaPeriodoId());
                timbre.setPersonaId(bitacoraTimbrado.getPersonaId());
                timbre.setFechaContrato(bitacoraTimbrado.getFechaContrato());
                timbre.setCentrocClienteId(bitacoraTimbrado.getClienteId());
                timbre.setIdentificadorOperacion(bitacoraTimbrado.getOperacionId());
                timbre.setCadenaOriginalSat(contenido.getCadenaOriginalSAT());
                timbre.setCfdi(contenido.getCfdi());
                timbre.setEstadoTimbreIdActual(3);
                timbre.setFechaTimbrado(contenido.getFechaTimbrado());
                timbre.setNoCertificadoCfdi(contenido.getNoCertificadoCFDI());
                timbre.setNoCertificadoSat(contenido.getNoCertificadoSAT());
                timbre.setQrCode(contenido.getQrCode());
                timbre.setSelloCfdi(contenido.getSelloCFDI());
                timbre.setSelloSat(contenido.getSelloSAT());
                timbre.setUuid(contenido.getUuid());
                timbre.setEsActual(true);
                NcrTimbre timbreGuardaddo = ncrTimbreRepository.save(timbre);

                RespuestaGenerica respuestaComprobante = comprobanteFiscalServices.guardarComprobanteFiscal(timbreGuardaddo);
                if(respuestaComprobante.isResultado()){
                    LOG.info(respuestaComprobante.getMensaje());
                }else {
                    LOG.error(respuestaComprobante.getMensaje());
                }

            }

            return new RespuestaGenerica(respuestaTimbrado, Constantes.RESULTADO_EXITO,Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " respuestaTimbrado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private boolean validaRespuestaTimbrado(ResultadoOperacion respuestaTimbrado){
        if(respuestaTimbrado.isExito()){
            List<Object> mapContenido = (List<Object>) respuestaTimbrado.getContenido();
            ContenidoCFDI contenido = ObjetoMapper.mapAll(mapContenido,ContenidoCFDI.class).get(0);
            if(contenido.getFechaTimbrado() != null
                    && contenido.getNoCertificadoCFDI() != null
                    && contenido.getNoCertificadoSAT() != null
                    && contenido.getQrCode() != null
                    && contenido.getSelloCFDI() != null
                    && contenido.getSelloSAT() != null
                    && contenido.getUuid() != null){
                return true;
            }
        }

        return false;
    }

    @Override
    public RespuestaGenerica resumen(Integer nominaPeriodoId, List<Integer> lista) throws ServiceException {
        try{
            Resumen resumen = new Resumen();
            Long numeroEmpleadosPagados = ncrBitacoraTimbradoRepository.
                    countByNominaPeriodoIdAndEsActualAndPersonaIdInListAndEstadoTimbreId(nominaPeriodoId,true,lista,3);
            resumen.setDispersados(numeroEmpleadosPagados);
            resumen.setEnviados(lista.size());
            return new RespuestaGenerica(resumen,Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " resumen " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private ResultadoOperacion clienteTimbrado(String servicio, String idCertificadoSelloDigital, String[] cfdi)
            throws ServiceException {
        try{
            Dispersion dispersion = new Dispersion();
            dispersion.setCsd_id(idCertificadoSelloDigital);
            dispersion.setServicio(servicio);
            dispersion.setFirma(true);
            dispersion.setCfdi(cfdi);
            dispersion.setUrl_callback(urlDevolucion);

            OkHttpClient cliente = new OkHttpClient();
            JSONObject json = new JSONObject(dispersion);
            RequestBody cuerpoSolicitud = RequestBody.create(json.toString(),null);
            Request solicitud = new Request.Builder()
                    .url("https://us-central1-cosmonaut-299500.cloudfunctions.net/cosmonaut-cfdi")
                    .put(cuerpoSolicitud)
                    .addHeader("Content-Type","application/json")
                    .build();
            Call llamada = cliente.newCall(solicitud);
            Response respuesta = llamada.execute();
            if (respuesta.isSuccessful()){
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(Objects.requireNonNull(respuesta.body()).string(), ResultadoOperacion.class);
            }
            return null;
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " clienteTimbrado " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private void guardaBitacora(Object contenido, List<NcrBitacoraTimbrado> listaBitacora){
        List<Object> map = (List<Object>) contenido;
        List<ContenidoTimbrado> lista = ObjetoMapper.mapAll(map,ContenidoTimbrado.class);
        listaBitacora.forEach(ncrBitacoraTimbrado -> {
            Optional<NcrBitacoraTimbrado> bitacoraTimbrado =
                    ncrBitacoraTimbradoRepository.findByNominaPeriodoIdAndClienteIdAndPersonaIdAndEsActual(
                            ncrBitacoraTimbrado.getNominaPeriodoId(), ncrBitacoraTimbrado.getClienteId(),
                            ncrBitacoraTimbrado.getPersonaId(), true);
            if (bitacoraTimbrado.isPresent()){
                bitacoraTimbrado.get().setEsActual(false);
                ncrBitacoraTimbradoRepository.update(bitacoraTimbrado.get());
            }
        });
        for (int j = 0; j < lista.size(); j++) {
            listaBitacora.get(j).setOperacionId(lista.get(j).getId_operacion());
        }
        listaBitacora.forEach(ncrBitacoraTimbrado -> ncrBitacoraTimbradoRepository.save(ncrBitacoraTimbrado));
    }

    private ResultadoOperacionTimbres clienteTimbres(String servicio) throws ServiceException {
        try{
            OkHttpClient cliente = Cliente.obtenOkHttpCliente();
            cliente.sslSocketFactory();
            Request solicitud = new Request.Builder()
                    .url("https://us-central1-cosmonaut-299500.cloudfunctions.net/cosmonaut-timbres?servicio="+ servicio)
                    .get()
                    .build();
            Call llamada = cliente.newCall(solicitud);
            Response respuesta = llamada.execute();
            if (respuesta.isSuccessful()){
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(respuesta.body().string(), ResultadoOperacionTimbres.class);
            }
            return new ResultadoOperacionTimbres();
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " clienteTimbres " + Constantes.ERROR_EXCEPCION, e);
        }
    }
}
