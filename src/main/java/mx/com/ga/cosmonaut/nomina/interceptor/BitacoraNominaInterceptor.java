package mx.com.ga.cosmonaut.nomina.interceptor;

import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.http.HttpResponse;
import lombok.SneakyThrows;
import mx.com.ga.cosmonaut.common.dto.RespuestaGenerica;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrBitacoraNomina;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrNominaXperiodo;
import mx.com.ga.cosmonaut.common.entity.calculo.NcrProcesoNomina;
import mx.com.ga.cosmonaut.common.repository.calculo.NcrBitacoraNominaRepository;
import mx.com.ga.cosmonaut.nomina.dto.orquestador.peticion.Nomina;
import mx.com.ga.cosmonaut.nomina.dto.orquestador.peticion.NominaOrdinaria;
import mx.com.ga.cosmonaut.orquestador.dto.respuesta.CalculoNominaRespuesta;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BitacoraNominaInterceptor implements MethodInterceptor<Object,Object> {

    @Inject
    private NcrBitacoraNominaRepository ncrBitacoraNominaRepository;

    @SneakyThrows
    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        HttpResponse<RespuestaGenerica> httRespuesta = (HttpResponse<RespuestaGenerica>) context.proceed();
        RespuestaGenerica respuesta = httRespuesta.getBody().get();
        if (respuesta.isResultado()) {

            String datosSesion = (String) context.getParameterValues()[1];
            Object objeto = respuesta.getDatos();
            Integer nominaXperiodoId = null;

            NcrNominaXperiodo nominaXperiodo;
            CalculoNominaRespuesta calculoNominaRespuesta;
            NcrProcesoNomina procesoNomina;

            if (objeto instanceof NcrNominaXperiodo){
                nominaXperiodo = (NcrNominaXperiodo) objeto;
                nominaXperiodoId = nominaXperiodo.getNominaXperiodoId();
            }else if (objeto instanceof CalculoNominaRespuesta){
                calculoNominaRespuesta = (CalculoNominaRespuesta) objeto;
                nominaXperiodoId = calculoNominaRespuesta.getNominaPeriodoId();
            }else if (objeto instanceof NcrProcesoNomina){
                procesoNomina = (NcrProcesoNomina) objeto;
                nominaXperiodoId = procesoNomina.getNominaXperiodoId().getNominaXperiodoId();
            }
            

            int d = datosSesion.lastIndexOf("=");
            String usuarioId = datosSesion.substring(d + 1);

            String metodo = context.getExecutableMethod().getName();

            Integer estadoNominaId = null;

            switch(metodo) {
                case "crear":
                    estadoNominaId = 1;
                    break;
                case "recalcular":
                case "calcular":
                case "calculaNominaFiniquitoLiquidacion":
                    estadoNominaId = 2;
                    break;
                case "dispersar":
                    estadoNominaId = 3;
                    break;
                case "timbrar":
                    estadoNominaId = 4;
                    break;
                case "concluir":
                    estadoNominaId = 5;
                    break;
                case "eliminar":
                    estadoNominaId = 8;
                    break;
            }

            if (ncrBitacoraNominaRepository.existsByNominaPeriodoId(nominaXperiodoId)){
                ncrBitacoraNominaRepository.updateByNominaPeriodoId(nominaXperiodoId,false);
            }

            NcrBitacoraNomina bitacoraNomina = new NcrBitacoraNomina();
            bitacoraNomina.setEsActual(true);
            bitacoraNomina.setEstadoNominaId(estadoNominaId);
            bitacoraNomina.setNominaPeriodoId(nominaXperiodoId);
            bitacoraNomina.setQuien(Integer.valueOf(usuarioId));
            ncrBitacoraNominaRepository.save(bitacoraNomina);

        }
        return httRespuesta;
    }
}
