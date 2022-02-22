package mx.com.ga.cosmonaut.nomina.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.dto.consultas.DispersionConsulta;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrBitacoraPago;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrEmpleadoXnomina;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrNominaXperiodo;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocClienteXproveedor;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrBitacoraPagoRepository;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrEmpleadoXnominaRepository;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrNominaXperiodoRepository;
import mx.com.ga.cosmonaut.common.repository.cliente.NclCentrocClienteXproveedorRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.DispersionTimbradoRepository;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.common.util.ObjetoMapper;
import mx.com.ga.cosmonaut.nomina.dto.DispersionDto;
import mx.com.ga.cosmonaut.nomina.dto.dispercion.peticion.Detalle;
import mx.com.ga.cosmonaut.nomina.dto.dispercion.peticion.Dispersion;
import mx.com.ga.cosmonaut.nomina.dto.dispercion.peticion.EmpleadoDispersion;
import mx.com.ga.cosmonaut.nomina.dto.dispercion.peticion.RfcPeticion;
import mx.com.ga.cosmonaut.nomina.dto.dispercion.respuesta.*;
import mx.com.ga.cosmonaut.nomina.service.DispersionServices;
import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.util.*;

@Singleton
public class DispersionServicesImpl implements DispersionServices {

    private static final Logger LOG = LoggerFactory.getLogger(DispersionServicesImpl.class);

    @Inject
    @Client("${servicio.dispersion-timbrado.host}")
    private RxHttpClient cliente;

    @Value("${servicio.dispersion-timbrado.dispersion.path}")
    private String contextoDispersion;

    @Value("${servicio.dispersion-timbrado.rfc.path}")
    private String contextoRfc;

    @Value("${servicio.dispersion-timbrado.urlCallback.dispersion.banpay}")
    private String urlDevolucionBanpay;

    @Value("${servicio.dispersion-timbrado.urlCallback.dispersion.stp}")
    private String urlDevolucionStp;

    @Inject
    private DispersionTimbradoRepository dispersionTimbradoRepository;

    @Inject
    private NcrBitacoraPagoRepository ncrBitacoraPagoRepository;

    @Inject
    private NcrEmpleadoXnominaRepository empleadoXnominaRepository;

    @Inject
    private NcrNominaXperiodoRepository ncrNominaXperiodoRepository;

    @Inject
    private NclCentrocClienteXproveedorRepository nclCentrocClienteXproveedorRepository;

    @Override
    public RespuestaGenerica dispersion(Integer nominaPeriodoId,List<DispersionDto> listaDispersion) throws ServiceException {
        try{
            Optional<NcrNominaXperiodo> nomina = ncrNominaXperiodoRepository.findById(nominaPeriodoId);

            if (nomina.isPresent()){
                List<NcrBitacoraPago> listaBitacoraPago =  new ArrayList<>();
                Dispersion dispersion = new Dispersion();
                Detalle[] detalles = new Detalle[listaDispersion.size()];
                int i = 0;

                Optional<NclCentrocClienteXproveedor> proveedor = nclCentrocClienteXproveedorRepository.
                        findByCentrocClienteIdCentrocClienteId(nomina.get().getCentrocClienteId());

                if (proveedor.isPresent() && proveedor.get().getProveedorDispersionId() != null){
                    for (DispersionDto dispersionDto : listaDispersion) {
                        Detalle detalle = new Detalle();
                        DispersionConsulta dispersionConsulta =
                                dispersionTimbradoRepository.consultaDispersionEmpleados(
                                        dispersionDto.getNominaPeriodoId(),4,
                                        dispersionDto.getFechaContrato(), dispersionDto.getPersonaId(), dispersionDto.getCentroClienteId());

                        NcrBitacoraPago bitacoraPago = new NcrBitacoraPago();
                        bitacoraPago.setClienteId(dispersionDto.getCentroClienteId().longValue());
                        bitacoraPago.setPersonaId(dispersionDto.getPersonaId().longValue());
                        bitacoraPago.setFechaContrato(dispersionDto.getFechaContrato());
                        bitacoraPago.setNominaXperiodoId(dispersionDto.getNominaPeriodoId().longValue());
                        bitacoraPago.setUsuarioId(dispersionDto.getUsuarioId().longValue());
                        bitacoraPago.setEsActual(true);
                        bitacoraPago.setEstadoPagoId(7L);
                        bitacoraPago.setTipoProveedorDispersionId(proveedor.get().getProveedorDispersionId().getProveedorDispersionId().longValue());
                        listaBitacoraPago.add(bitacoraPago);

                        Long consecutivo = ncrBitacoraPagoRepository.countByNominaXperiodoIdAndPersonaId(
                                dispersionDto.getNominaPeriodoId(),
                                dispersionDto.getPersonaId()) + 1;

                        switch (proveedor.get().getProveedorDispersionId().getProveedorDispersionId()){
                            case 1:
                                detalle.setCuenta_ordenante("646180162805300004");
                                detalle.setCuenta_beneficiario("646180110400000007");
                                detalle.setNombre_ordenante("Lynher");
                                detalle.setInstitucion_contraparte(90646);

                                break;
                            case 2:
                                detalle.setCuenta_ordenante("653180003810033227");
                                detalle.setCuenta_beneficiario(dispersionConsulta.getCuentaBeneficiario());
                                detalle.setNombre_ordenante(limpiarString(dispersionConsulta.getNombreOrdenante()));
                                detalle.setInstitucion_contraparte(846);
                                break;
                            default:
                                return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR,Constantes.ERROR_PROVEEDOR_NO_INTEGRADO);
                        }

                        detalle.setNombre_beneficiario(limpiarString(dispersionConsulta.getNombreBeneficiario()));

                        detalle.setReferencia_numerica(1234567);

                        detalle.setClave_rastreo("R" + dispersionDto.getCentroClienteId().toString() +
                                dispersionDto.getPersonaId().toString() +
                                dispersionDto.getNominaPeriodoId().toString() +
                                consecutivo);

                        detalle.setInstitucion_operante(90646);

                        detalle.setRfc_curp_ordenante(dispersionConsulta.getRfcCurpOrdenante());
                        detalle.setRfc_curp_beneficiario(dispersionConsulta.getRfcCurpBeneficiario());

                        detalle.setTipo_cuenta_ordenante(dispersionConsulta.getTipoCuentaOrdenante());
                        detalle.setTipo_cuenta_beneficiario(dispersionConsulta.getTipoCuentaBeneficiario());

                        detalle.setTipo_pago(dispersionConsulta.getTipoPago());
                        detalle.setConcepto_pago(dispersionConsulta.getConceptoPago());
                        detalle.setMonto(0.01);

                        if (detalle.getCuenta_beneficiario() == null || detalle.getCuenta_beneficiario().isEmpty()
                                || detalle.getCuenta_ordenante() == null || detalle.getCuenta_ordenante().isEmpty()
                                || detalle.getInstitucion_contraparte() == null
                                || detalle.getInstitucion_operante() == null){
                            return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR,Constantes.ERROR_DISPERSION_EMPLEADOS_BANCO);
                        }

                        detalles[i] = detalle;
                        i++;
                    }

                    dispersion.setServicio(proveedor.get().getProveedorDispersionId().getDescripcion());
                    String urlRetorno = proveedor.get().getProveedorDispersionId().getProveedorDispersionId() == 1 ? urlDevolucionStp : urlDevolucionBanpay;
                    dispersion.setUrl_callback(urlRetorno);
                    dispersion.setDetalle(detalles);

                    ResultadoOperacion resultadoOperacion;

                    OkHttpClient cliente = new OkHttpClient();
                    JSONObject json = new JSONObject(dispersion);
                    RequestBody cuerpoSolicitud = RequestBody.create(json.toString(),null);
                    Request solicitud = new Request.Builder()
                            .url("https://us-central1-cosmonaut-299500.cloudfunctions.net/cosmonaut-dispersion-async")
                            .put(cuerpoSolicitud)
                            .addHeader("Content-Type","application/json")
                            .build();
                    Call llamada = cliente.newCall(solicitud);
                    Response respuesta = llamada.execute();
                    if (respuesta.isSuccessful()){
                        ObjectMapper objectMapper = new ObjectMapper();
                        resultadoOperacion =
                                objectMapper.readValue(Objects.requireNonNull(respuesta.body()).string(), ResultadoOperacion.class);
                    }else {
                        return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR,"En el servicio de dispersion");
                    }

                    this.guardaBitacora(resultadoOperacion.getContenido(), listaBitacoraPago);
                    ncrNominaXperiodoRepository.updateByNominaXperiodoId(nomina.get().getNominaXperiodoId(),6);

                    return new RespuestaGenerica(resultadoOperacion, Constantes.RESULTADO_EXITO,Constantes.EXITO);
                }else {
                    return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR,Constantes.ERROR_DISPERSION_PROVEEDOR);
                }
            }
            return new RespuestaGenerica(null, Constantes.RESULTADO_ERROR,Constantes.ERROR_OBTENER_NOMINA);

        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " dispersion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica obtenerRfc(RfcPeticion rfcPeticion) throws ServiceException {
        try{
            ResultadoOperacion resultadoOperacion =
                    cliente.retrieve(HttpRequest.POST(contextoRfc, rfcPeticion), ResultadoOperacion.class).blockingFirst();
            return new RespuestaGenerica(resultadoOperacion, Constantes.RESULTADO_EXITO,Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " obtenerRfc " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica respuestaBANPAY(String respuesta) throws ServiceException {
        try{
            LOG.info("Se recibe repuesta de respuestaBANPAY");
            ObjectMapper objectMapper = new ObjectMapper();
            RespuestaDispersion respuestaDispersion = objectMapper.readValue(respuesta, RespuestaDispersion.class);
            Resultado resultado = respuestaDispersion.getOutput();
            ncrBitacoraPagoRepository.updateByOperacionId(
                    resultado.getId_operacion(),
                    resultado.getExito() ? 5L : 2L,
                    resultado.getDescripcionError(),
                    resultado.getClaveRastreo(),
                    resultado.getId(),
                    resultado.getExito());

            NcrBitacoraPago pago = ncrBitacoraPagoRepository.findByOperacionId(resultado.getId_operacion());

            NcrEmpleadoXnomina empleado = empleadoXnominaRepository.
                    findByNominaXperiodoIdNominaXperiodoIdAndPersonaIdPersonaId(pago.getNominaXperiodoId().intValue(),
                            pago.getPersonaId().intValue());

            empleadoXnominaRepository.updateByNominaXperiodoIdAndPersonaId(
                    empleado.getNominaXperiodoId(),
                    empleado.getPersonaId(), resultado.getExito() ? 5 : 2, null);

            return new RespuestaGenerica(respuestaDispersion, Constantes.RESULTADO_EXITO,Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " respuestaBANPAY " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica respuestaSTP(String respuesta) throws ServiceException {
        try{
            LOG.info("Se recibe repuesta respuestaSTP");
            ObjectMapper objectMapper = new ObjectMapper();
            RespuestaDispersion respuestaDispersion = objectMapper.readValue(respuesta, RespuestaDispersion.class);
            Resultado resultado = respuestaDispersion.getOutput();

            ncrBitacoraPagoRepository.updateByOperacionId(
                    resultado.getId_operacion(),
                    resultado.getExito() ? 1L : 2L,
                    resultado.getDescripcionError(),
                    resultado.getClaveRastreo(),
                    resultado.getId(),
                    resultado.getExito());

            NcrBitacoraPago pago = ncrBitacoraPagoRepository.findByOperacionId(resultado.getId_operacion());

            NcrEmpleadoXnomina empleado = empleadoXnominaRepository.
                    findByNominaXperiodoIdNominaXperiodoIdAndPersonaIdPersonaId(pago.getNominaXperiodoId().intValue(),
                            pago.getPersonaId().intValue());

            empleadoXnominaRepository.updateByNominaXperiodoIdAndPersonaId(
                    empleado.getNominaXperiodoId(),
                    empleado.getPersonaId(), resultado.getExito() ? 1 : 2,null);

            return new RespuestaGenerica(respuestaDispersion, Constantes.RESULTADO_EXITO,Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " respuestaSTP " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica respuestaComplementariaSTP(String respuesta) throws ServiceException {
        try{
            LOG.info("Se recibe repuesta respuestaComplementariaSTP");
            LOG.info("Respuesta-> " + respuesta);
            ObjectMapper objectMapper = new ObjectMapper();
            ResultadoSTP resultadoSTP = objectMapper.readValue(respuesta, ResultadoSTP.class);

            Long estado;

            switch (resultadoSTP.getEstado()){
                case 1:
                    estado = 5L;
                    break;
                case 2:
                    estado = 3L;
                    break;
                default:
                    estado = 4L;
                    break;
            }

            ncrBitacoraPagoRepository.updateByIdProveedor(
                    resultadoSTP.getId(),
                    resultadoSTP.getEstado() == 1 ? true : false,
                    estado,
                    resultadoSTP.getCausaDevolucion());

            NcrBitacoraPago pago = ncrBitacoraPagoRepository.findByIdProveedor(resultadoSTP.getId());

            NcrEmpleadoXnomina empleado = empleadoXnominaRepository.
                    findByNominaXperiodoIdNominaXperiodoIdAndPersonaIdPersonaId(pago.getNominaXperiodoId().intValue(),
                            pago.getPersonaId().intValue());

            empleadoXnominaRepository.updateByNominaXperiodoIdAndPersonaId(
                    empleado.getNominaXperiodoId(),
                    empleado.getPersonaId(),estado.intValue(),null);

            return new RespuestaGenerica(null, Constantes.RESULTADO_EXITO,Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " respuestaComplementariaSTP " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica procesando(Integer nominaPeriodoId, List<Long> lista) throws ServiceException {
        try{
            long numeroEmpleadosPagados = ncrBitacoraPagoRepository.
                    countByNominaXperiodoIdAndPersonaIdInListAndEstadoPagoIdNotEqualsAndEsActual(nominaPeriodoId,lista,7,true);
            Long porcentaje = (100 * numeroEmpleadosPagados) / lista.size();
            return new RespuestaGenerica(porcentaje, Constantes.RESULTADO_EXITO,Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " procesando " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica resumen(Integer nominaPeriodoId, List<Long> lista) throws ServiceException {
        try{
            Resumen resumen = new Resumen();
            List<Integer> listaEstados = Arrays.asList(1,5);
            Long numeroEmpleadosPagados = ncrBitacoraPagoRepository.
                    countByNominaXperiodoIdAndPersonaIdInListAndEstadoPagoIdInListAndEsActual(nominaPeriodoId,lista,listaEstados,true);
            resumen.setDispersados(numeroEmpleadosPagados);
            resumen.setEnviados(lista.size());
            return new RespuestaGenerica(resumen,Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " resumen " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    @Override
    public RespuestaGenerica modificarEstatusEmpleadoDispersion(List<EmpleadoDispersion> empleadoDispersion) throws ServiceException {
        try{
            empleadoDispersion.forEach(empleado -> {
                empleadoXnominaRepository.updateByNominaXperiodoIdAndPersonaId(
                        empleado.getNominaXperiodoId(),
                        empleado.getPersonaId(), 5,null);
                ncrNominaXperiodoRepository.updateByNominaXperiodoId(empleado.getNominaXperiodoId().getNominaXperiodoId(),6);
                NcrBitacoraPago bitacoraPago = new NcrBitacoraPago();
                bitacoraPago.setNominaXperiodoId(empleado.getNominaXperiodoId().getNominaXperiodoId().longValue());
                bitacoraPago.setPersonaId(empleado.getPersonaId().getPersonaId().longValue());
                bitacoraPago.setFechaContrato(empleado.getFechaContrato());
                bitacoraPago.setClienteId(empleado.getClienteId().getCentrocClienteId().longValue());
                bitacoraPago.setFechaDispersion(new Timestamp(new Date().getTime()));
                bitacoraPago.setEstadoPagoId(5L);

                //bitacoraPago.setTipoProveedorDispersionId(3L);
                
                bitacoraPago.setEsActual(true);
                ncrBitacoraPagoRepository.save(bitacoraPago);
            });
            return new RespuestaGenerica(null, Constantes.RESULTADO_EXITO, Constantes.EXITO);
        }catch (Exception e){
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " modificarEstatusEmpleadoDispersion " + Constantes.ERROR_EXCEPCION, e);
        }
    }

    private void guardaBitacora(Object contenido, List<NcrBitacoraPago> listaBitacora){
        List<Object> map = (List<Object>) contenido;
        List<ContenidoDispersion> lista = ObjetoMapper.mapAll(map,ContenidoDispersion.class);
        listaBitacora.forEach(ncrBitacoraPago -> {
            Optional<NcrBitacoraPago> bitacoraPago =
                    ncrBitacoraPagoRepository.findByNominaXperiodoIdAndClienteIdAndPersonaIdAndEsActual(
                            ncrBitacoraPago.getNominaXperiodoId().intValue(), ncrBitacoraPago.getClienteId().intValue(),
                            ncrBitacoraPago.getPersonaId().intValue(), true);
            if (bitacoraPago.isPresent()){
                bitacoraPago.get().setEsActual(false);
                ncrBitacoraPagoRepository.update(bitacoraPago.get());
            }
        });
        for (int j = 0; j < lista.size(); j++) {
            listaBitacora.get(j).setOperacionId(lista.get(j).getId_operacion());
        }
        listaBitacora.forEach(ncrBitacoraTimbrado -> ncrBitacoraPagoRepository.save(ncrBitacoraTimbrado));
    }

    private String limpiarString(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return texto;
    }

    private void clienteDispersion(){

    }

}
