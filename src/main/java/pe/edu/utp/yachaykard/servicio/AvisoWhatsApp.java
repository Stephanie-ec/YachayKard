package pe.edu.utp.yachaykard.servicio;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Aviso al apoderado por WhatsApp (RF-28). Arma el mensaje ya redactado y el enlace wa.me
 * con el celular del apoderado; el tutor solo toca "Enviar" en WhatsApp.
 * Se usa un enlace y no un envío automático porque la API de WhatsApp Business es de pago.
 */
public class AvisoWhatsApp {

    /**
     * Código de país del Perú.
     */
    static final String CODIGO_PERU = "51";
    static final String TIPO_FALTAS = "Semana de faltas";
    static final String TIPO_RIESGO = "Riesgo alto";
    private static final Pattern CELULAR = Pattern.compile("9\\d{8}");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Deja solo los 9 dígitos del celular. Acepta espacios, guiones y el prefijo +51.
     *
     * @param telefono celular registrado del apoderado
     * @return 9 dígitos que empiezan con 9
     * @throws IllegalArgumentException si no es un celular peruano válido
     */
    public String normalizarCelular(String telefono) {
        String digitos = telefono == null ? "" : telefono.replaceAll("\\D", "");
        if (digitos.length() == 11 && digitos.startsWith(CODIGO_PERU)) {
            digitos = digitos.substring(2);
        }
        if (!CELULAR.matcher(digitos).matches()) {
            throw new IllegalArgumentException("El apoderado no tiene un celular válido "
                    + "(9 dígitos que empiezan con 9). Actualícelo en Estudiantes.");
        }
        return digitos;
    }

    /**
     * @param telefono celular registrado
     * @return true si se puede armar el enlace de WhatsApp
     */
    public boolean esCelularValido(String telefono) {
        try {
            normalizarCelular(telefono);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Arma el enlace que abre WhatsApp con el mensaje escrito.
     *
     * @param telefono celular del apoderado
     * @param mensaje  texto del mensaje
     * @return enlace https://wa.me/51XXXXXXXXX?text=...
     * @throws IllegalArgumentException si el celular no es válido o el mensaje está vacío
     */
    public String enlace(String telefono, String mensaje) {
        String celular = normalizarCelular(telefono);
        if (mensaje == null || mensaje.isBlank()) {
            throw new IllegalArgumentException("El mensaje está vacío.");
        }
        String texto = URLEncoder.encode(mensaje.trim(), StandardCharsets.UTF_8).replace("+", "%20");
        return "https://wa.me/" + CODIGO_PERU + celular + "?text=" + texto;
    }

    /**
     * Mensaje para una incidencia Moderada o Grave.
     *
     * @param apoderado  nombre del apoderado
     * @param estudiante nombre del estudiante
     * @param colegio    nombre de la institución
     * @param tipo       tipo de incidencia
     * @param gravedad   Moderada o Grave
     * @param fechaHora  cuándo ocurrió
     * @param accion     acción tomada por el colegio
     * @return texto del mensaje
     */
    public String mensajeIncidencia(String apoderado, String estudiante, String colegio, String tipo,
                                    String gravedad, LocalDateTime fechaHora, String accion) {
        String fecha = fechaHora == null ? "" : " el " + fechaHora.format(FECHA);
        return "Estimado(a) " + apoderado + ": le informamos que " + estudiante
                + " tuvo una incidencia " + gravedad.toLowerCase() + fecha + " (" + tipo + "). "
                + "Acción tomada: " + accion + ". Le pedimos comunicarse con el tutor. "
                + "Atentamente, tutoría de " + colegio + ".";
    }

    /**
     * Mensaje para una alerta de faltas o de riesgo alto.
     *
     * @param apoderado  nombre del apoderado
     * @param estudiante nombre del estudiante
     * @param colegio    nombre de la institución
     * @param tipoAlerta "Semana de faltas" o "Riesgo alto"
     * @return texto del mensaje
     * @throws IllegalArgumentException si el tipo de alerta no se avisa al apoderado
     */
    public String mensajeAlerta(String apoderado, String estudiante, String colegio, String tipoAlerta) {
        String detalle;
        if (TIPO_FALTAS.equals(tipoAlerta)) {
            detalle = " tiene varias faltas sin justificar en la última semana.";
        } else if (TIPO_RIESGO.equals(tipoAlerta)) {
            detalle = " necesita acompañamiento por su asistencia, sus notas o su conducta.";
        } else {
            throw new IllegalArgumentException("Esta alerta no se avisa al apoderado.");
        }
        return "Estimado(a) " + apoderado + ": le informamos que " + estudiante + detalle
                + " Le pedimos comunicarse con el tutor. Atentamente, tutoría de " + colegio + ".";
    }
}
