package mx.com.ga.cosmonaut.nomina.service.impl;

import mx.com.ga.cosmonaut.common.dto.consultas.DeduccionConsulta;
import mx.com.ga.cosmonaut.common.dto.consultas.PercepcionConsulta;
import mx.com.ga.cosmonaut.common.dto.consultas.TimbradoConsulta;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrBitacoraTimbrado;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrEmpleadoXnomina;
import mx.com.ga.cosmonaut.common.entity.cliente.NclCentrocClienteXproveedor;
import mx.com.ga.cosmonaut.common.entity.colaborador.NcoContratoColaborador;
import mx.com.ga.cosmonaut.common.exception.ServiceException;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrEmpleadoXnominaRepository;
import mx.com.ga.cosmonaut.common.repository.colaborador.NcoContratoColaboradorRepository;
import mx.com.ga.cosmonaut.common.repository.nativo.TimbradoRepository;
import mx.com.ga.cosmonaut.common.service.ReporteService;
import mx.com.ga.cosmonaut.common.util.Constantes;
import mx.com.ga.cosmonaut.nomina.dto.DispersionDto;
import mx.com.ga.cosmonaut.nomina.dto.TimbradoDto;
import mx.com.ga.cosmonaut.nomina.service.CfdiService;
import mx.com.ga.cosmonaut.orquestador.entity.Antiguedad;
import mx.com.ga.cosmonaut.orquestador.entity.HorasExtras;
import mx.com.ga.cosmonaut.orquestador.entity.Incapacidades;
import mx.com.ga.cosmonaut.orquestador.repository.NominaRepository;
import mx.com.ga.cosmonaut.orquestador.util.OrquestadorLibUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Singleton
public class CfdiServiceImpl implements CfdiService {

    private static final Logger LOG = LoggerFactory.getLogger(CfdiServiceImpl.class);

    @Inject
    private ReporteService reporteService;

    @Inject
    private TimbradoRepository timbradoRepository;

    @Inject
    private NcoContratoColaboradorRepository contratoColaboradorRepository;

    @Inject
    private NominaRepository nominaRepository;

    @Inject
    private NcrEmpleadoXnominaRepository ncrEmpleadoXnominaRepository;

    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat formatoFechaActual = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private String folio;
    private String serie;

    @Override
    public TimbradoDto genera(List<DispersionDto> lista, NclCentrocClienteXproveedor proveedor) throws ServiceException {
        try {
            TimbradoDto timbradoDto = new TimbradoDto();
            String[] xml = new String[lista.size()];
            List<NcrBitacoraTimbrado> listaBitacora = new ArrayList<>();
            int i = 0;
            for (DispersionDto dispersion :lista) {
                DocumentBuilderFactory crearDocumento = DocumentBuilderFactory.newInstance();
                crearDocumento.setAttribute(XMLConstants.FEATURE_SECURE_PROCESSING, true);
                crearDocumento.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                crearDocumento.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
                crearDocumento.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                crearDocumento.setFeature("http://xml.org/sax/features/external-general-entities", false);
                crearDocumento.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
                crearDocumento.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                crearDocumento.setXIncludeAware(false);
                crearDocumento.setExpandEntityReferences(false);

                NcoContratoColaborador colaborador = contratoColaboradorRepository.
                        findByFechaContratoAndPersonaIdPersonaIdAndCentrocClienteIdCentrocClienteId(dispersion.getFechaContrato(),
                                dispersion.getPersonaId(),dispersion.getCentroClienteId())
                        .orElseThrow(() -> new ServiceException(Constantes.ERROR_OBTENER_EMPLEADO));

                serie = dispersion.getNominaPeriodoId().toString()
                        + colaborador.getCentrocClienteId().getCentrocClienteId().toString()
                        + colaborador.getPersonaId().getPersonaId().toString();

                folio = dispersion.getNominaPeriodoId().toString()
                        + colaborador.getCentrocClienteId().getCentrocClienteId().toString()
                        + colaborador.getPersonaId().getRfc();


                DocumentBuilder documento = crearDocumento.newDocumentBuilder();
                Document cfdi = documento.newDocument();

                TimbradoConsulta timbradoConsulta = timbradoRepository.consultaInformacionTimbrado(dispersion.getNominaPeriodoId(),
                        dispersion.getPersonaId());

                listaBitacora.add(obtenBitacora(timbradoConsulta, dispersion,proveedor.getProveedorTimbradoId().getProveedorTimbradoId()));


                List<PercepcionConsulta> listaPercepcion = timbradoRepository.
                        consultaPercepcion(dispersion.getNominaPeriodoId(), dispersion.getPersonaId());

                Element comprobante = generaComprobante(cfdi,timbradoConsulta,listaPercepcion);
                cfdi.appendChild(comprobante);
                comprobante.appendChild(generaEmisor(cfdi, timbradoConsulta));
                comprobante.appendChild(generaReceptor(cfdi, timbradoConsulta));
                comprobante.appendChild(generaConceptos(cfdi,timbradoConsulta,listaPercepcion));
                comprobante.appendChild(generaComplemento(cfdi,timbradoConsulta, dispersion,listaPercepcion,colaborador));
                StringWriter escritor = new StringWriter();
                escritor = reporteService.generaXML(cfdi, escritor);
                xml[i] = escritor.toString().
                        replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>","");
                LOG.info("XML -> " + escritor.toString());
                i++;
            }

            timbradoDto.setXmls(xml);
            timbradoDto.setListaBitacora(listaBitacora);
            return timbradoDto;
        } catch (ParserConfigurationException | ParseException pce) {
            throw new ServiceException(Constantes.ERROR_CLASE + this.getClass().getSimpleName()
                    + Constantes.ERROR_METODO + " generaCFDI " + Constantes.ERROR_EXCEPCION, pce);
        }
    }

    private NcrBitacoraTimbrado obtenBitacora(TimbradoConsulta timbradoConsulta, DispersionDto dispersionDto, Integer proveedor){
        NcrBitacoraTimbrado bitacoraTimbrado = new NcrBitacoraTimbrado();
        bitacoraTimbrado.setClienteId(timbradoConsulta.getCentrocClienteId());
        bitacoraTimbrado.setEsActual(Constantes.ESTATUS_ACTIVO);
        bitacoraTimbrado.setEsCorrecto(false);
        bitacoraTimbrado.setEstadoTimbreId(1);
        bitacoraTimbrado.setFechaContrato(timbradoConsulta.getFechaContratoNogrupo());
        bitacoraTimbrado.setNominaPeriodoId(timbradoConsulta.getNominaXperiodoId());
        bitacoraTimbrado.setPersonaId(timbradoConsulta.getPersonaId());
        bitacoraTimbrado.setTipoProveedorTimbradoId(proveedor);
        bitacoraTimbrado.setUsuarioId(dispersionDto.getUsuarioId());
        return bitacoraTimbrado;
    }

    private Element generaComprobante(Document cfdi, TimbradoConsulta timbrado, List<PercepcionConsulta> lista){

        Double totalOtrosPagos = OrquestadorLibUtil.redondeaDouble(lista.stream()
                .filter(p -> p.getTipoPercepcion().equals("999") && !p.getClave().equals("002"))
                .mapToDouble(v -> Double.valueOf(v.getMontoTotal())).sum());

        Element comprobante = cfdi.createElement("cfdi:Comprobante");

        Attr atributoCfdi = cfdi.createAttribute("xmlns:cfdi");
        atributoCfdi.setValue("http://www.sat.gob.mx/cfd/3");
        comprobante.setAttributeNode(atributoCfdi);

        Attr atributoNomina12 = cfdi.createAttribute("xmlns:nomina12");
        atributoNomina12.setValue("http://www.sat.gob.mx/nomina12");
        comprobante.setAttributeNode(atributoNomina12);

        Attr atributoXsi = cfdi.createAttribute("xmlns:xsi");
        atributoXsi.setValue("http://www.w3.org/2001/XMLSchema-instance");
        comprobante.setAttributeNode(atributoXsi);

        Attr atributoSchemaLocation = cfdi.createAttribute("xsi:schemaLocation");
        atributoSchemaLocation.setValue("http://www.sat.gob.mx/cfd/3 http://www.sat.gob.mx/sitio_internet/cfd/3/cfdv33.xsd http://www.sat.gob.mx/nomina12 http://www.sat.gob.mx/sitio_internet/cfd/nomina/nomina12.xsd" );
        comprobante.setAttributeNode(atributoSchemaLocation);

        Attr atributoVersion = cfdi.createAttribute("Version");
        atributoVersion.setValue("3.3");
        comprobante.setAttributeNode(atributoVersion);

        Attr atributoSerie = cfdi.createAttribute("Serie");
        atributoSerie.setValue(serie);
        comprobante.setAttributeNode(atributoSerie);

        Attr atributoFolio = cfdi.createAttribute("Folio");
        atributoFolio.setValue(folio);
        comprobante.setAttributeNode(atributoFolio);

        Instant nowUtc = Instant.now();
        ZoneId zid = ZoneId.of(timbrado.getZona());
        ZonedDateTime zdt = ZonedDateTime.ofInstant(nowUtc,zid);
        System.out.println(zdt.minusSeconds(15).format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss")));

        Attr atributoFecha = cfdi.createAttribute("Fecha");
        atributoFecha.setValue(zdt.minusSeconds(15).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        comprobante.setAttributeNode(atributoFecha);

        Attr atributoFormaPago = cfdi.createAttribute("FormaPago");
        atributoFormaPago.setValue("99");
        comprobante.setAttributeNode(atributoFormaPago);

        Double totalGravadoSueldos = OrquestadorLibUtil.redondeaDouble(
                lista.stream().filter(p -> !p.getTipoPercepcion().equals("022") && !p.getTipoPercepcion().equals("023")
                                &&  !p.getTipoPercepcion().equals("025") &&  !p.getTipoPercepcion().equals("039"))
                        .mapToDouble(v -> Double.valueOf(v.getImporteGravable())).sum());

        Double totalExcentoSueldos = OrquestadorLibUtil.redondeaDouble(
                lista.stream().filter(p -> !p.getTipoPercepcion().equals("022") &&  !p.getTipoPercepcion().equals("023")
                                &&  !p.getTipoPercepcion().equals("025") &&  !p.getTipoPercepcion().equals("039"))
                        .mapToDouble(v -> Double.valueOf(v.getImporteExento())).sum());

        Double totalSueldos = Math.abs(OrquestadorLibUtil.
                redondeaDouble( totalGravadoSueldos + totalExcentoSueldos));

        Double subTotal = OrquestadorLibUtil.redondeaDouble(totalSueldos + totalOtrosPagos);
        Attr atributoSubTotal = cfdi.createAttribute("SubTotal");
        comprobante.setAttributeNode(atributoSubTotal);

        Double total = OrquestadorLibUtil.redondeaDouble(subTotal - Double.valueOf(timbrado.getDescuento()));
        Attr atributoTotal = cfdi.createAttribute("Total");
        comprobante.setAttributeNode(atributoTotal);

        List<PercepcionConsulta> listaPercepcionesIndenmizacion =
                lista.stream().filter(p -> p.getTipoPercepcion().equals("022") ||  p.getTipoPercepcion().equals("023")
                        ||  p.getTipoPercepcion().equals("025") ||  p.getTipoPercepcion().equals("039")).collect(Collectors.toList());

        if (!listaPercepcionesIndenmizacion.isEmpty()){
            Double totalSeparacionIndemnizacion = OrquestadorLibUtil.redondeaDouble(listaPercepcionesIndenmizacion.stream()
                    .mapToDouble(v -> Double.valueOf(v.getImporteExento()) + Double.valueOf(v.getImporteGravable())).sum());

            Double totalPercepciones = OrquestadorLibUtil.redondeaDouble(totalSueldos + totalSeparacionIndemnizacion);

            Double totalIn =  OrquestadorLibUtil.redondeaDouble(
                    totalPercepciones - Double.valueOf(timbrado.getDescuento()));

            atributoSubTotal.setValue(totalPercepciones.toString());
            atributoTotal.setValue(totalIn.toString());
        }else{
            atributoTotal.setValue(OrquestadorLibUtil.redondeaDoubleStrin(total));
            atributoSubTotal.setValue(OrquestadorLibUtil.redondeaDoubleStrin(subTotal));
        }


        Attr atributoDescuento = cfdi.createAttribute("Descuento");
        atributoDescuento.setValue(timbrado.getDescuento());
        comprobante.setAttributeNode(atributoDescuento);

        Attr atributoMoneda = cfdi.createAttribute("Moneda");
        atributoMoneda.setValue(timbrado.getMoneda());
        comprobante.setAttributeNode(atributoMoneda);


        Attr atributoTipoDeComprobante = cfdi.createAttribute("TipoDeComprobante");
        atributoTipoDeComprobante.setValue(timbrado.getTipoDeComprobante());
        comprobante.setAttributeNode(atributoTipoDeComprobante);

        Attr atributoMetodoPago = cfdi.createAttribute("MetodoPago");
        atributoMetodoPago.setValue(timbrado.getMetodoPago());
        comprobante.setAttributeNode(atributoMetodoPago);

        Attr atributoLugarExpedicion = cfdi.createAttribute("LugarExpedicion");
        atributoLugarExpedicion.setValue(timbrado.getLugarExpedicion());
        comprobante.setAttributeNode(atributoLugarExpedicion);

        return comprobante;
    }

    private Element generaConceptos(Document cfdi, TimbradoConsulta timbrado,List<PercepcionConsulta> lista){

        Double totalOtrosPagos = OrquestadorLibUtil.redondeaDouble(lista.stream()
                .filter(p -> p.getTipoPercepcion().equals("999") && !p.getClave().equals("002"))
                .mapToDouble(v -> Double.valueOf(v.getMontoTotal())).sum());

        Element conceptos = cfdi.createElement("cfdi:Conceptos");

        Element concepto = cfdi.createElement("cfdi:Concepto");
        conceptos.appendChild(concepto);

        Attr atributoConceptoClaveProdServ = cfdi.createAttribute("ClaveProdServ");
        atributoConceptoClaveProdServ.setValue(timbrado.getClaveProdServ());
        concepto.setAttributeNode(atributoConceptoClaveProdServ);

        Attr atributoConceptoCantidad = cfdi.createAttribute("Cantidad");
        atributoConceptoCantidad.setValue(timbrado.getCantidad());
        concepto.setAttributeNode(atributoConceptoCantidad);

        Attr atributoConceptoClaveUnidad = cfdi.createAttribute("ClaveUnidad");
        atributoConceptoClaveUnidad.setValue(timbrado.getClaveUnidad());
        concepto.setAttributeNode(atributoConceptoClaveUnidad);

        Attr atributoConceptoDescripcion = cfdi.createAttribute("Descripcion");
        atributoConceptoDescripcion.setValue("Pago de nómina");
        concepto.setAttributeNode(atributoConceptoDescripcion);

        Double totalGravado = OrquestadorLibUtil.redondeaDouble(
                lista.stream().filter(p -> !p.getTipoPercepcion().equals("022") && !p.getTipoPercepcion().equals("023")
                                && !p.getTipoPercepcion().equals("025") && !p.getTipoPercepcion().equals("039"))
                        .mapToDouble(v -> Double.valueOf(v.getImporteGravable())).sum());

        Double totalExcento = OrquestadorLibUtil.redondeaDouble(
                lista.stream().filter(p -> !p.getTipoPercepcion().equals("022") && !p.getTipoPercepcion().equals("023")
                                &&  !p.getTipoPercepcion().equals("025") && !p.getTipoPercepcion().equals("039"))
                        .mapToDouble(v -> Double.valueOf(v.getImporteExento())).sum());

        Double total = Math.abs(OrquestadorLibUtil.
                redondeaDouble( totalGravado + totalExcento));

        Double valorUnitario = OrquestadorLibUtil.redondeaDouble(total + totalOtrosPagos);

        Attr atributoConceptoValorUnitario = cfdi.createAttribute("ValorUnitario");
        atributoConceptoValorUnitario.setValue(valorUnitario.toString());
        concepto.setAttributeNode(atributoConceptoValorUnitario);

        Attr atributoConceptoImporte= cfdi.createAttribute("Importe");
        concepto.setAttributeNode(atributoConceptoImporte);

        List<PercepcionConsulta> listaPercepcionesIndenmizacion =
                lista.stream().filter(p -> p.getTipoPercepcion().equals("022") ||  p.getTipoPercepcion().equals("023")
                        ||  p.getTipoPercepcion().equals("025") ||  p.getTipoPercepcion().equals("039"))
                        .collect(Collectors.toList());

        if (!listaPercepcionesIndenmizacion.isEmpty()){
            Double totalSeparacionIndemnizacion = OrquestadorLibUtil.redondeaDouble(listaPercepcionesIndenmizacion.stream()
                    .mapToDouble(v -> Double.valueOf(v.getImporteExento()) + Double.valueOf(v.getImporteGravable())).sum());

            Double totalGravadoSueldos = OrquestadorLibUtil.redondeaDouble(
                    lista.stream().filter(p -> !p.getTipoPercepcion().equals("022") && !p.getTipoPercepcion().equals("023")
                                    &&  !p.getTipoPercepcion().equals("025") &&  !p.getTipoPercepcion().equals("039"))
                            .mapToDouble(v -> Double.valueOf(v.getImporteGravable())).sum());

            Double totalExcentoSueldos = OrquestadorLibUtil.redondeaDouble(
                    lista.stream().filter(p -> !p.getTipoPercepcion().equals("022") &&  !p.getTipoPercepcion().equals("023")
                                    &&  !p.getTipoPercepcion().equals("025") &&  !p.getTipoPercepcion().equals("039"))
                            .mapToDouble(v -> Double.valueOf(v.getImporteExento())).sum());

            Double totalSueldos = Math.abs(OrquestadorLibUtil.
                    redondeaDouble( totalGravadoSueldos + totalExcentoSueldos));

            Double totalPercepciones = OrquestadorLibUtil.redondeaDouble(totalSueldos + totalSeparacionIndemnizacion);

            atributoConceptoValorUnitario.setValue(OrquestadorLibUtil.redondeaDoubleStrin(totalPercepciones));
            atributoConceptoImporte.setValue(OrquestadorLibUtil.redondeaDoubleStrin(totalPercepciones));
        }else{
            atributoConceptoValorUnitario.setValue(OrquestadorLibUtil.redondeaDoubleStrin(valorUnitario));
            atributoConceptoImporte.setValue(OrquestadorLibUtil.redondeaDoubleStrin(valorUnitario));
        }


        Attr atributoConceptoDescuento= cfdi.createAttribute("Descuento");
        atributoConceptoDescuento.setValue(timbrado.getDescuento());
        concepto.setAttributeNode(atributoConceptoDescuento);

        return conceptos;
    }

    private Element generaReceptor(Document cfdi, TimbradoConsulta timbrado){
        Element receptor = cfdi.createElement("cfdi:Receptor");

        Attr atributoReceptorRfc = cfdi.createAttribute("Rfc");
        atributoReceptorRfc.setValue(timbrado.getReceptorRfc());
        receptor.setAttributeNode(atributoReceptorRfc);

        Attr atributoReceptorNombre = cfdi.createAttribute("Nombre");
        atributoReceptorNombre.setValue(timbrado.getReceptorNombre());
        receptor.setAttributeNode(atributoReceptorNombre);

        Attr atributoUsoCFDI = cfdi.createAttribute("UsoCFDI");
        atributoUsoCFDI.setValue(timbrado.getReceptorUsoCFDI());
        receptor.setAttributeNode(atributoUsoCFDI);

        return receptor;
    }

    private Element generaEmisor(Document cfdi, TimbradoConsulta timbrado){
        Element emisor = cfdi.createElement("cfdi:Emisor");

        Attr atributoEmisorRfc = cfdi.createAttribute("Rfc");
        atributoEmisorRfc.setValue(timbrado.getEmisorRfc());
        emisor.setAttributeNode(atributoEmisorRfc);

        Attr atributoEmisorNombre = cfdi.createAttribute("Nombre");
        atributoEmisorNombre.setValue(timbrado.getEmisorNombre());
        emisor.setAttributeNode(atributoEmisorNombre);

        Attr atributoEmisorRegimenFiscal = cfdi.createAttribute("RegimenFiscal");
        atributoEmisorRegimenFiscal.setValue(timbrado.getEmisorRegimenFiscal());
        emisor.setAttributeNode(atributoEmisorRegimenFiscal);

        return emisor;
    }

    private Element generaComplemento(Document cfdi, TimbradoConsulta timbrado, DispersionDto dispersionDto,
                                      List<PercepcionConsulta> lista, NcoContratoColaborador colaborador) throws ServiceException, ParseException {
        Element complemento = cfdi.createElement("cfdi:Complemento");
        complemento.appendChild(generaNomina12Nomina(cfdi, timbrado,dispersionDto, lista,colaborador));
        return complemento;
    }

    private Element generaNomina12Nomina(Document cfdi, TimbradoConsulta timbrado, DispersionDto dispersionDto,
                                         List<PercepcionConsulta> lista, NcoContratoColaborador colaborador) throws ServiceException, ParseException {

        Double totalOtrosPagos = OrquestadorLibUtil.redondeaDouble(lista.stream()
                .filter(p -> p.getTipoPercepcion().equals("999") && !p.getClave().equals("002"))
                .mapToDouble(v -> Double.valueOf(v.getMontoTotal())).sum());

        Element nomina12Nomina = cfdi.createElement("nomina12:Nomina");

        Attr atributoFechaFinalPago = cfdi.createAttribute("FechaFinalPago");
        atributoFechaFinalPago.setValue(formatoFecha.format(timbrado.getFechaFinalPago()));
        nomina12Nomina.setAttributeNode(atributoFechaFinalPago);

        Attr atributoFechaInicialPago = cfdi.createAttribute("FechaInicialPago");
        atributoFechaInicialPago.setValue(formatoFecha.format(timbrado.getFechaInicialPago()));
        nomina12Nomina.setAttributeNode(atributoFechaInicialPago);

        Attr atributoFechaPago = cfdi.createAttribute("FechaPago");
        atributoFechaPago.setValue(formatoFecha.format(timbrado.getFechaPago()));
        nomina12Nomina.setAttributeNode(atributoFechaPago);

        Attr atributoNumDiasPagados = cfdi.createAttribute("NumDiasPagados");
        atributoNumDiasPagados.setValue(timbrado.getNumDiasPagados());
        nomina12Nomina.setAttributeNode(atributoNumDiasPagados);

        if(Double.valueOf(timbrado.getTotalDeducciones()) > 0){
            Attr atributoTotalDeducciones = cfdi.createAttribute("TotalDeducciones");
            atributoTotalDeducciones.setValue(timbrado.getTotalDeducciones());
            nomina12Nomina.setAttributeNode(atributoTotalDeducciones);
        }

        Attr atributoTotalOtrosPagos = cfdi.createAttribute("TotalOtrosPagos");
        atributoTotalOtrosPagos.setValue(totalOtrosPagos.toString());
        nomina12Nomina.setAttributeNode(atributoTotalOtrosPagos);


        Attr atributoVersion = cfdi.createAttribute("Version");
        atributoVersion.setValue(timbrado.getVersion());
        nomina12Nomina.setAttributeNode(atributoVersion);

        Attr atributoTipoNomina = cfdi.createAttribute("TipoNomina");
        atributoTipoNomina.setValue(timbrado.getTipoNomina());
        nomina12Nomina.setAttributeNode(atributoTipoNomina);

        List<DeduccionConsulta> listaDeducciones = timbradoRepository.
                consultaDecepcion(dispersionDto.getNominaPeriodoId(), dispersionDto.getPersonaId());

        List<DeduccionConsulta> listaIncapacidades = listaDeducciones.stream()
                .filter(deduccion -> deduccion.getTipoDeduccion().equals("006")).collect(Collectors.toList());


        List<PercepcionConsulta> listaHorasExtras = lista.stream()
                .filter(deduccion -> deduccion.getTipoPercepcion().equals("019")).collect(Collectors.toList());

        nomina12Nomina.appendChild(generaNomina12Emisor(cfdi, timbrado));
        nomina12Nomina.appendChild(generaNomina12Receptor(cfdi, timbrado));
        nomina12Nomina.appendChild(generaNomina12Percepciones(cfdi, timbrado, dispersionDto, lista,nomina12Nomina));

        List<DeduccionConsulta> listaDeduccionesEnCero =
                listaDeducciones.stream().filter(d -> Double.valueOf(d.getImporte()) > 0.0 ).collect(Collectors.toList());

        if(!listaDeduccionesEnCero.isEmpty()){
            nomina12Nomina.appendChild(generaNomina12Deducciones(cfdi, timbrado, dispersionDto,listaDeducciones));
        }

        if(colaborador.getTipoRegimenContratacionId().getTipoRegimenContratacionId().equals("2")){
            nomina12Nomina.appendChild(generaNomina12OtrosPagos(cfdi, lista));
        }

        if (!listaIncapacidades.isEmpty()){
            List<Incapacidades> listadoIncapacidades = null;
            try {
                listadoIncapacidades = nominaRepository.obtenIncapacidades(timbrado.getNominaXperiodoId(),timbrado.getPersonaId());
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            nomina12Nomina.appendChild(generaNomina12Incapacidades(cfdi, listadoIncapacidades,timbrado));
        }

        /**if (!listaHorasExtras.isEmpty()){
            List<HorasExtras> listadoHorasExtras =
                    nominaRepository.obtenHorasExtras(timbrado.getNominaXperiodoId(),timbrado.getPersonaId());

            generaNomina12HorasExtras(cfdi, listadoHorasExtras,timbrado,nomina12Nomina);
        }*/

        return nomina12Nomina;
    }

    private Element generaNomina12Emisor(Document cfdi, TimbradoConsulta timbrado){
        Element nomina12Emisor = cfdi.createElement("nomina12:Emisor");

        Attr atributoRegistroPatronal = cfdi.createAttribute("RegistroPatronal");
        atributoRegistroPatronal.setValue(timbrado.getRegistroPatronal());
        nomina12Emisor.setAttributeNode(atributoRegistroPatronal);

        if (timbrado.getEmisorCurp() != null && !timbrado.getEmisorCurp().isEmpty()){
            Attr atributoCurp = cfdi.createAttribute("Curp");
            atributoCurp.setValue(timbrado.getEmisorCurp());
            nomina12Emisor.setAttributeNode(atributoCurp);
        }

        return nomina12Emisor;
    }

    private Element generaNomina12Receptor(Document cfdi, TimbradoConsulta timbrado) throws ParseException, ServiceException {
        Element nomina12Receptor = cfdi.createElement("nomina12:Receptor");

        Attr atributoAntiguedad = cfdi.createAttribute("Antigüedad");
        atributoAntiguedad.setValue(obtenAntiguedad(timbrado.getFechaFinalPago(), timbrado.getFechaInicioRelLaboral()));
        nomina12Receptor.setAttributeNode(atributoAntiguedad);

        Attr atributoClaveEntFed = cfdi.createAttribute("ClaveEntFed");
        atributoClaveEntFed.setValue(timbrado.getClaveEntFed());
        nomina12Receptor.setAttributeNode(atributoClaveEntFed);

        if (timbrado.getMetodoPagoId() == 4 || timbrado.getMetodoPagoId() == 5){
            Attr atributoCuentaBancaria = cfdi.createAttribute("CuentaBancaria");
            atributoCuentaBancaria.setValue(timbrado.getCuentaBancaria());
            nomina12Receptor.setAttributeNode(atributoCuentaBancaria);
        }

        Attr atributoCurp = cfdi.createAttribute("Curp");
        atributoCurp.setValue(timbrado.getCurp());
        nomina12Receptor.setAttributeNode(atributoCurp);

        Attr atributoNumSeguridadSocial = cfdi.createAttribute("NumSeguridadSocial");
        atributoNumSeguridadSocial.setValue(timbrado.getNumSeguridadSocial());
        nomina12Receptor.setAttributeNode(atributoNumSeguridadSocial);

        Attr atributoFechaInicioRelLaboral = cfdi.createAttribute("FechaInicioRelLaboral");
        atributoFechaInicioRelLaboral.setValue(formatoFecha.format(timbrado.getFechaInicioRelLaboral()));
        nomina12Receptor.setAttributeNode(atributoFechaInicioRelLaboral);

        Attr atributoTipoContrato = cfdi.createAttribute("TipoContrato");
        atributoTipoContrato.setValue(timbrado.getTipoContrato());
        nomina12Receptor.setAttributeNode(atributoTipoContrato);

        Attr atributoSindicalizado = cfdi.createAttribute("Sindicalizado");
        atributoSindicalizado.setValue(timbrado.isSindicalizado() ? "Sí" : "No");
        nomina12Receptor.setAttributeNode(atributoSindicalizado);

        Attr atributoTipoJornada = cfdi.createAttribute("TipoJornada");
        atributoTipoJornada.setValue(timbrado.getTipoJornada());
        nomina12Receptor.setAttributeNode(atributoTipoJornada);

        Attr atributoTipoRegimen = cfdi.createAttribute("TipoRegimen");
        atributoTipoRegimen.setValue(timbrado.getTipoRegimen());
        nomina12Receptor.setAttributeNode(atributoTipoRegimen);

        Attr atributoNumEmpleado = cfdi.createAttribute("NumEmpleado");
        atributoNumEmpleado.setValue(timbrado.getNumEmpleado());
        nomina12Receptor.setAttributeNode(atributoNumEmpleado);

        Attr atributoDepartamento = cfdi.createAttribute("Departamento");
        atributoDepartamento.setValue(timbrado.getDepartamento());
        nomina12Receptor.setAttributeNode(atributoDepartamento);

        Attr atributoPuesto = cfdi.createAttribute("Puesto");
        atributoPuesto.setValue(timbrado.getPuesto());
        nomina12Receptor.setAttributeNode(atributoPuesto);

        Attr atributoRiesgoPuesto = cfdi.createAttribute("RiesgoPuesto");
        atributoRiesgoPuesto.setValue(timbrado.getRiesgoPuesto());
        nomina12Receptor.setAttributeNode(atributoRiesgoPuesto);

        Attr atributoPeriodicidadPago = cfdi.createAttribute("PeriodicidadPago");
        if (timbrado.getTipoNomina().equals("E")){
            atributoPeriodicidadPago.setValue("99");
        }else{
            atributoPeriodicidadPago.setValue(timbrado.getPeriodicidadPago());
        }
        nomina12Receptor.setAttributeNode(atributoPeriodicidadPago);

        Double salarioBaseCotizacion = OrquestadorLibUtil.redondeaDouble(Double.valueOf(timbrado.getSalarioBaseCotApor()));

        Attr atributoSalarioBaseCotApor = cfdi.createAttribute("SalarioBaseCotApor");
        atributoSalarioBaseCotApor.setValue(salarioBaseCotizacion.toString());
        nomina12Receptor.setAttributeNode(atributoSalarioBaseCotApor);


        Double salarioDiarioIntegrado = OrquestadorLibUtil.redondeaDouble(Double.valueOf(timbrado.getSalarioBaseCotApor()));

        Attr atributoSalarioDiarioIntegrado = cfdi.createAttribute("SalarioDiarioIntegrado");
        atributoSalarioDiarioIntegrado.setValue(salarioDiarioIntegrado.toString());
        nomina12Receptor.setAttributeNode(atributoSalarioDiarioIntegrado);

        return nomina12Receptor;
    }

    private Element generaNomina12Percepciones(Document cfdi, TimbradoConsulta timbrado, DispersionDto dispersionDto,
                                               List<PercepcionConsulta> lista,Element nomina12Nomina) throws ServiceException {

        Element nomina12Percepciones = cfdi.createElement("nomina12:Percepciones");

        List<PercepcionConsulta> listaPercepciones =
                lista.stream().filter(percepcionConsulta -> percepcionConsulta.getOtroPago() == null).collect(Collectors.toList());

        List<PercepcionConsulta> listaPercepcionesIndenmizacion =
                lista.stream().filter(p -> p.getTipoPercepcion().equals("022") ||  p.getTipoPercepcion().equals("023")
                                ||  p.getTipoPercepcion().equals("025") ||  p.getTipoPercepcion().equals("039"))
                        .collect(Collectors.toList());

        if (!listaPercepcionesIndenmizacion.isEmpty()){
            Double totalSeparacionIndemnizacion = OrquestadorLibUtil.redondeaDouble(listaPercepcionesIndenmizacion.stream()
                    .mapToDouble(v -> Double.valueOf(v.getImporteExento()) + Double.valueOf(v.getImporteGravable())).sum());

            Double totalGravadoSueldos = OrquestadorLibUtil.redondeaDouble(
                    lista.stream().filter(p -> !p.getTipoPercepcion().equals("022") && !p.getTipoPercepcion().equals("023")
                                    && !p.getTipoPercepcion().equals("025") && !p.getTipoPercepcion().equals("039"))
                            .mapToDouble(v -> Double.valueOf(v.getImporteGravable())).sum());

            Double totalExcentoSueldos = OrquestadorLibUtil.redondeaDouble(
                    lista.stream().filter(p -> !p.getTipoPercepcion().equals("022") && !p.getTipoPercepcion().equals("023")
                                    &&  !p.getTipoPercepcion().equals("025") && !p.getTipoPercepcion().equals("039"))
                            .mapToDouble(v -> Double.valueOf(v.getImporteExento())).sum());

            Double totalGravado = OrquestadorLibUtil.redondeaDouble(
                    lista.stream().mapToDouble(v -> Double.valueOf(v.getImporteGravable())).sum());

            Double totalExcento = OrquestadorLibUtil.redondeaDouble(
                    lista.stream().mapToDouble(v -> Double.valueOf(v.getImporteExento())).sum());

            Double totalSueldos = Math.abs(OrquestadorLibUtil.
                    redondeaDouble( totalGravadoSueldos + totalExcentoSueldos));

            Attr atributoTotalSeparacionIndemnizacion = cfdi.createAttribute("TotalSeparacionIndemnizacion");
            atributoTotalSeparacionIndemnizacion.setValue(totalSeparacionIndemnizacion.toString());
            nomina12Percepciones.setAttributeNode(atributoTotalSeparacionIndemnizacion);

            Double totalPercepciones = OrquestadorLibUtil.redondeaDouble(totalSueldos + totalSeparacionIndemnizacion);

            Attr atributoTotalPercepciones = cfdi.createAttribute("TotalPercepciones");
            atributoTotalPercepciones.setValue(totalPercepciones.toString());
            nomina12Nomina.setAttributeNode(atributoTotalPercepciones);

            Attr atributoTotalExento = cfdi.createAttribute("TotalExento");
            atributoTotalExento.setValue(totalExcento.toString());
            nomina12Percepciones.setAttributeNode(atributoTotalExento);

            Attr atributoTotalGravado = cfdi.createAttribute("TotalGravado");
            atributoTotalGravado.setValue(totalGravado.toString());
            nomina12Percepciones.setAttributeNode(atributoTotalGravado);

            Attr atributoTotalSueldos = cfdi.createAttribute("TotalSueldos");
            atributoTotalSueldos.setValue(totalSueldos.toString());
            nomina12Percepciones.setAttributeNode(atributoTotalSueldos);

        }else{

            Double totalGravadoSueldos = OrquestadorLibUtil.redondeaDouble(
                    lista.stream().filter(p -> !p.getTipoPercepcion().equals("022") && !p.getTipoPercepcion().equals("023")
                                    && !p.getTipoPercepcion().equals("025") && !p.getTipoPercepcion().equals("039"))
                            .mapToDouble(v -> Double.valueOf(v.getImporteGravable())).sum());

            Double totalExcentoSueldos = OrquestadorLibUtil.redondeaDouble(
                    lista.stream().filter(p -> !p.getTipoPercepcion().equals("022") && !p.getTipoPercepcion().equals("023")
                                    &&  !p.getTipoPercepcion().equals("025") && !p.getTipoPercepcion().equals("039"))
                            .mapToDouble(v -> Double.valueOf(v.getImporteExento())).sum());

            Double totalGravado = OrquestadorLibUtil.redondeaDouble(
                    lista.stream().mapToDouble(v -> Double.valueOf(v.getImporteGravable())).sum());

            Double totalExcento = OrquestadorLibUtil.redondeaDouble(
                    lista.stream().mapToDouble(v -> Double.valueOf(v.getImporteExento())).sum());

            Double totalSueldos = Math.abs(OrquestadorLibUtil.
                    redondeaDouble( totalGravadoSueldos + totalExcentoSueldos));

            Attr atributoTotalPercepciones = cfdi.createAttribute("TotalPercepciones");
            atributoTotalPercepciones.setValue(totalSueldos.toString());
            nomina12Nomina.setAttributeNode(atributoTotalPercepciones);

            Attr atributoTotalExento = cfdi.createAttribute("TotalExento");
            atributoTotalExento.setValue(totalExcento.toString());
            nomina12Percepciones.setAttributeNode(atributoTotalExento);

            Attr atributoTotalGravado = cfdi.createAttribute("TotalGravado");
            atributoTotalGravado.setValue(totalGravado.toString());
            nomina12Percepciones.setAttributeNode(atributoTotalGravado);

            Attr atributoTotalSueldos = cfdi.createAttribute("TotalSueldos");
            atributoTotalSueldos.setValue(totalSueldos.toString());
            nomina12Percepciones.setAttributeNode(atributoTotalSueldos);
        }

        AtomicBoolean esIndemnizacion = new AtomicBoolean(false);
        listaPercepciones.forEach(percepcionConsulta -> {
            try {
                nomina12Percepciones.appendChild(generaNomina12Percepcione(cfdi, percepcionConsulta,timbrado));
                if(percepcionConsulta.getTipoPercepcion().equals("022") || percepcionConsulta.getTipoPercepcion().equals("023")
                        || percepcionConsulta.getTipoPercepcion().equals("025") ||  percepcionConsulta.getTipoPercepcion().equals("039")){
                    esIndemnizacion.set(true);
                }
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        });


        if(esIndemnizacion.get()){
            Double totalSeparacionIndemnizacion = OrquestadorLibUtil.redondeaDouble(listaPercepcionesIndenmizacion.stream()
                    .mapToDouble(v -> Double.valueOf(v.getImporteExento()) + Double.valueOf(v.getImporteGravable())).sum());
            generaNomina12Indemnizacion(cfdi, totalSeparacionIndemnizacion.toString(),timbrado,nomina12Percepciones);
        }
        return nomina12Percepciones;
    }

    private Element generaNomina12Percepcione(Document cfdi, PercepcionConsulta percepcion,TimbradoConsulta timbrado) throws ServiceException {

        Element nomina12Percepcione = cfdi.createElement("nomina12:Percepcion");

        Attr atributoTipoPercepcion = cfdi.createAttribute("TipoPercepcion");
        atributoTipoPercepcion.setValue(percepcion.getClave());
        nomina12Percepcione.setAttributeNode(atributoTipoPercepcion);

        Attr atributoClave = cfdi.createAttribute("Clave");
        atributoClave.setValue(percepcion.getTipoPercepcion());
        nomina12Percepcione.setAttributeNode(atributoClave);

        Attr atributoConcepto = cfdi.createAttribute("Concepto");
        atributoConcepto.setValue(percepcion.getConcepto());
        nomina12Percepcione.setAttributeNode(atributoConcepto);

        Attr atributoImporteGravado = cfdi.createAttribute("ImporteGravado");
        atributoImporteGravado.setValue(percepcion.getImporteGravable());
        nomina12Percepcione.setAttributeNode(atributoImporteGravado);

        Attr atributoImporteExento = cfdi.createAttribute("ImporteExento");
        atributoImporteExento.setValue(percepcion.getImporteExento());
        nomina12Percepcione.setAttributeNode(atributoImporteExento);

        if(percepcion.getTipoPercepcion().equals("019")){
            List<HorasExtras> listadoHorasExtras = null;
            try {
                listadoHorasExtras = nominaRepository.obtenHorasExtras(timbrado.getNominaXperiodoId(),timbrado.getPersonaId());
            } catch (ServiceException e) {
                e.printStackTrace();
            }
            generaNomina12HorasExtras(cfdi, listadoHorasExtras,timbrado,nomina12Percepcione);
        }

        return nomina12Percepcione;
    }

    private Element generaNomina12Deducciones(Document cfdi, TimbradoConsulta timbrado, DispersionDto dispersionDto, List<DeduccionConsulta> lista)
            throws ServiceException {

        DeduccionConsulta deduccion = lista.stream().
                filter(d -> d.getClave().equals("002")).findAny().orElse(null);

        Double totalOtrasDeducciones = OrquestadorLibUtil.redondeaDouble(lista.stream().filter(d -> !d.getClave().equals("002")).
                mapToDouble(value -> Double.valueOf(value.getImporte())).sum());

        Element nomina12Deducciones = cfdi.createElement("nomina12:Deducciones");

        if (deduccion != null && Double.valueOf(deduccion.getImporte()) > 0.0 ){
            Attr atributoTotalImpuestosRetenidos = cfdi.createAttribute("TotalImpuestosRetenidos");
            atributoTotalImpuestosRetenidos.setValue(deduccion.getImporte());
            nomina12Deducciones.setAttributeNode(atributoTotalImpuestosRetenidos);
        }

        Attr atributoTotalOtrasDeducciones = cfdi.createAttribute("TotalOtrasDeducciones");
        atributoTotalOtrasDeducciones.setValue(totalOtrasDeducciones.toString());
        nomina12Deducciones.setAttributeNode(atributoTotalOtrasDeducciones);

        List<DeduccionConsulta> listaDeducciones =
                lista.stream().filter(d -> Double.valueOf(d.getImporte()) > 0.0 ).collect(Collectors.toList());

        listaDeducciones.forEach(deduccionConsulta -> {
            nomina12Deducciones.appendChild(generaNomina12Deduccion(cfdi, deduccionConsulta));
        });

        return nomina12Deducciones;
    }

    private Element generaNomina12Deduccion(Document cfdi, DeduccionConsulta deduccionConsulta){

        Element nomina12Deduccion = cfdi.createElement("nomina12:Deduccion");

        Attr atributoTipoDeduccion = cfdi.createAttribute("TipoDeduccion");
        atributoTipoDeduccion.setValue(deduccionConsulta.getTipoDeduccion());
        nomina12Deduccion.setAttributeNode(atributoTipoDeduccion);

        Attr atributoClave = cfdi.createAttribute("Clave");
        atributoClave.setValue(deduccionConsulta.getClave());
        nomina12Deduccion.setAttributeNode(atributoClave);

        Attr atributoConcepto = cfdi.createAttribute("Concepto");
        atributoConcepto.setValue(deduccionConsulta.getConcepto());
        nomina12Deduccion.setAttributeNode(atributoConcepto);

        Attr atributoImporte = cfdi.createAttribute("Importe");
        atributoImporte.setValue(deduccionConsulta.getImporte());
        nomina12Deduccion.setAttributeNode(atributoImporte);

        return nomina12Deduccion;
    }

    private Element generaNomina12OtrosPagos(Document cfdi, List<PercepcionConsulta> lista){

        Element nomina12OtrosPagos = cfdi.createElement("nomina12:OtrosPagos");

        List<PercepcionConsulta> listaOtrosPagos =
                lista.stream().filter(p -> p.getOtroPago() != null)
                        .collect(Collectors.toList());

        listaOtrosPagos.forEach(otrosPagos -> {
            nomina12OtrosPagos.appendChild(generaNomina12OtroPago(cfdi,otrosPagos));
        });

        return nomina12OtrosPagos;
    }

    private Element generaNomina12OtroPago(Document cfdi, PercepcionConsulta otrosPagos){

        Element nomina12OtroPago = cfdi.createElement("nomina12:OtroPago");

        Attr atributoTipoOtroPago = cfdi.createAttribute("TipoOtroPago");
        atributoTipoOtroPago.setValue(otrosPagos.getOtroPago());
        nomina12OtroPago.setAttributeNode(atributoTipoOtroPago);

        Attr atributoClave = cfdi.createAttribute("Clave");
        atributoClave.setValue(otrosPagos.getClave());
        nomina12OtroPago.setAttributeNode(atributoClave);

        Attr atributoConcepto = cfdi.createAttribute("Concepto");
        atributoConcepto.setValue(otrosPagos.getConcepto());
        nomina12OtroPago.setAttributeNode(atributoConcepto);

        Attr atributoImporte = cfdi.createAttribute("Importe");
        atributoImporte.setValue(otrosPagos.getClave().equals("002") ? "0.0" : otrosPagos.getMontoTotal());
        nomina12OtroPago.setAttributeNode(atributoImporte);

        nomina12OtroPago.appendChild(generaNomina12SubsidioAlEmpleo(cfdi, otrosPagos.getMontoTotal()));

        return nomina12OtroPago;
    }

    private Element generaNomina12SubsidioAlEmpleo(Document cfdi, String monto){

        Element nomina12SubsidioAlEmpleo = cfdi.createElement("nomina12:SubsidioAlEmpleo");

        Attr atributoSubsidioCausado = cfdi.createAttribute("SubsidioCausado");
        atributoSubsidioCausado.setValue(monto);
        nomina12SubsidioAlEmpleo.setAttributeNode(atributoSubsidioCausado);

        return nomina12SubsidioAlEmpleo;
    }

    private Element generaNomina12Incapacidades(Document cfdi, List<Incapacidades> lista, TimbradoConsulta timbrado){

        Element nomina12Incapacidades = cfdi.createElement("nomina12:Incapacidades");

        lista.forEach(incapacidades -> {
            nomina12Incapacidades.appendChild(generaNomina12Incapacidad(cfdi,incapacidades,timbrado));
        });

        return nomina12Incapacidades;
    }

    private Element generaNomina12Incapacidad(Document cfdi, Incapacidades incapacidad,TimbradoConsulta timbrado){

        Element nomina12OIncapacidad = cfdi.createElement("nomina12:Incapacidad");

        Attr atributoDiasIncapacidad = cfdi.createAttribute("DiasIncapacidad");
        atributoDiasIncapacidad.setValue(incapacidad.getDiasAplicados());
        nomina12OIncapacidad.setAttributeNode(atributoDiasIncapacidad);

        Attr atributoTipoIncapacidad = cfdi.createAttribute("TipoIncapacidad");
        atributoTipoIncapacidad.setValue(incapacidad.getTipoIncapacidad());
        nomina12OIncapacidad.setAttributeNode(atributoTipoIncapacidad);

        Attr atributoDescuento = cfdi.createAttribute("ImporteMonetario");
        atributoDescuento.setValue(OrquestadorLibUtil.redondeaDoubleStrin(Double.valueOf(incapacidad.getMonto())));
        nomina12OIncapacidad.setAttributeNode(atributoDescuento);

        return nomina12OIncapacidad;
    }

    private void generaNomina12HorasExtras(Document cfdi, List<HorasExtras> lista, TimbradoConsulta timbrado,Element nomina12Nomina){
        lista.forEach(horaExtra -> {
            //<nomina12:HorasExtra Dias="4" HorasExtra="3" ImportePagado="217.46" TipoHoras="01"/>
            Element nomina12HorasExtras = cfdi.createElement("nomina12:HorasExtra");

            Attr atributoDias = cfdi.createAttribute("Dias");
            atributoDias.setValue(horaExtra.getDiasAplicados());
            nomina12HorasExtras.setAttributeNode(atributoDias);

            Attr atributoHorasExtra = cfdi.createAttribute("HorasExtra");
            atributoHorasExtra.setValue(horaExtra.getDiasAplicados());
            nomina12HorasExtras.setAttributeNode(atributoHorasExtra);

            Attr atributoImportePagado = cfdi.createAttribute("ImportePagado");
            atributoImportePagado.setValue(horaExtra.getMonto());
            nomina12HorasExtras.setAttributeNode(atributoImportePagado);

            String tipoHora;
            if(horaExtra.getTipoHoras().equals("HE2")){
                tipoHora = "01";
            }else if (horaExtra.getTipoHoras().equals("HE3")){
                tipoHora = "02";
            }else {
                tipoHora = "03";
            }

            Attr atributoTipoHoras = cfdi.createAttribute("TipoHoras");
            atributoTipoHoras.setValue(tipoHora);
            nomina12HorasExtras.setAttributeNode(atributoTipoHoras);

            nomina12Nomina.appendChild(nomina12HorasExtras);
        });

    }


    private void generaNomina12Indemnizacion(Document cfdi, String totalSeparacionIndemnizacion, TimbradoConsulta timbrado,Element nomina12Nomina) throws ServiceException {

        Element nomina12SeparacionIndemnizacion = cfdi.createElement("nomina12:SeparacionIndemnizacion");
        Attr atributoTotalPagado = cfdi.createAttribute("TotalPagado");
        atributoTotalPagado.setValue(totalSeparacionIndemnizacion);
        nomina12SeparacionIndemnizacion.setAttributeNode(atributoTotalPagado);

        Antiguedad antiguedad = nominaRepository.obtenAntiguedad(timbrado.getFechaFinalPago(),timbrado.getFechaInicioRelLaboral());

        Optional<NcrEmpleadoXnomina> empleadoXnomina = ncrEmpleadoXnominaRepository.
                findByMaxEmpleadoNomina(timbrado.getPersonaId());

        Attr atributoNumAniosServicio = cfdi.createAttribute("NumAñosServicio");
        atributoNumAniosServicio.setValue(antiguedad.getAnio().toString());
        nomina12SeparacionIndemnizacion.setAttributeNode(atributoNumAniosServicio);

        Attr atributoUltimoSueldoMensOrd = cfdi.createAttribute("UltimoSueldoMensOrd");
        nomina12SeparacionIndemnizacion.setAttributeNode(atributoUltimoSueldoMensOrd);

        if(empleadoXnomina.isPresent()){
            atributoUltimoSueldoMensOrd.setValue(empleadoXnomina.get().getTotalNeto().toString());
        }else{
            atributoUltimoSueldoMensOrd.setValue("0.00");
        }

        Attr atributoIngresoAcumulable = cfdi.createAttribute("IngresoNoAcumulable");
        atributoIngresoAcumulable.setValue("0.00");
        nomina12SeparacionIndemnizacion.setAttributeNode(atributoIngresoAcumulable);

        Attr atributoIngresoNoAcumulable = cfdi.createAttribute("IngresoAcumulable");
        atributoIngresoNoAcumulable.setValue("0.00");
        nomina12SeparacionIndemnizacion.setAttributeNode(atributoIngresoNoAcumulable);

        nomina12Nomina.appendChild(nomina12SeparacionIndemnizacion);
    }


    private Element generaNomina12HorasExtra(Document cfdi, PercepcionConsulta horasExtras,TimbradoConsulta timbrado){

        Element nomina12Nomina12HoraExtra = cfdi.createElement("nomina12:TipoHoras");

        Attr atributoDiasIncapacidad = cfdi.createAttribute("DiasIncapacidad");
        atributoDiasIncapacidad.setValue(timbrado.getDiasIncapacidad());
        nomina12Nomina12HoraExtra.setAttributeNode(atributoDiasIncapacidad);

        Attr atributoTipoIncapacidad = cfdi.createAttribute("HorasExtra");
        atributoTipoIncapacidad.setValue(timbrado.getTipoIncapacidadId());
        nomina12Nomina12HoraExtra.setAttributeNode(atributoTipoIncapacidad);

        Attr atributoDescuento = cfdi.createAttribute("ImportePagado");
        atributoDescuento.setValue(horasExtras.getImporteExento());
        nomina12Nomina12HoraExtra.setAttributeNode(atributoDescuento);

        return nomina12Nomina12HoraExtra;
    }

    private String obtenAntiguedad (Date fechaFinalPago, Date fechaAntiguedad) throws ServiceException {

        Calendar fechaHasta = Calendar.getInstance();
        fechaHasta.setTime(fechaFinalPago);
        fechaHasta.add(Calendar.DAY_OF_MONTH,1);

        Antiguedad antiguedad = nominaRepository.obtenAntiguedad(fechaFinalPago,fechaAntiguedad);
        StringBuilder sAntiguedad = new StringBuilder(100);
        sAntiguedad.append("P");
        if (antiguedad.getAnio() > 0){
            sAntiguedad.append(antiguedad.getAnio()).append("Y");
        }

        if (antiguedad.getMes() != 0){
            sAntiguedad.append(antiguedad.getMes()).append("M");
        }

        if (antiguedad.getDia() != 0){
            sAntiguedad.append(antiguedad.getDia()).append("D");
        }

        return sAntiguedad.toString();
    }

}
