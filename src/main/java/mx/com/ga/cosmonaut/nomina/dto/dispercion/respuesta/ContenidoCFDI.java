package mx.com.ga.cosmonaut.nomina.dto.dispercion.respuesta;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ContenidoCFDI {

    private String cadenaOriginalSAT;
    private String cfdi;
    private String fechaTimbrado;
    private String noCertificadoCFDI;
    private String noCertificadoSAT;
    private String qrCode;
    private String selloCFDI;
    private String selloSAT;
    private String uuid;

}
