package pe.edu.utp.yachaykard.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cifrado de contraseñas con PBKDF2 y sal aleatoria (RNF-02).
 * <p>
 * La contraseña nunca se guarda tal cual: se guarda un texto con el formato
 * {@code pbkdf2$iteraciones$sal$hash}, donde la sal y el hash van en Base64.
 * Al iniciar sesión se vuelve a calcular el hash con la misma sal y se compara.
 * Solo usa clases del JDK, sin dependencias externas.
 */
public final class CifradoClave {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIJO = "pbkdf2";
    private static final String SEPARADOR = "$";
    private static final int ITERACIONES = 600_000;
    private static final int BYTES_SAL = 16;
    private static final int BITS_HASH = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private CifradoClave() {
        // Clase de utilidad: no se instancia.
    }

    /**
     * Cifra una contraseña con una sal nueva.
     *
     * @param clave contraseña en texto plano
     * @return texto a guardar en la columna usuario.password
     */
    public static String cifrar(String clave) {
        if (clave == null || clave.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        byte[] sal = new byte[BYTES_SAL];
        ALEATORIO.nextBytes(sal);
        byte[] hash = calcular(clave.toCharArray(), sal, ITERACIONES);
        Base64.Encoder base64 = Base64.getEncoder();
        return PREFIJO + SEPARADOR + ITERACIONES + SEPARADOR
                + base64.encodeToString(sal) + SEPARADOR + base64.encodeToString(hash);
    }

    /**
     * Comprueba si una contraseña coincide con el texto guardado.
     *
     * @param clave    contraseña que escribió el usuario
     * @param guardado valor de la columna usuario.password
     * @return true si coincide; false si no coincide o si el valor guardado no tiene el formato esperado
     */
    public static boolean verificar(String clave, String guardado) {
        if (clave == null || guardado == null) {
            return false;
        }
        String[] partes = guardado.split("\\$");
        if (partes.length != 4 || !PREFIJO.equals(partes[0])) {
            return false;
        }
        try {
            int iteraciones = Integer.parseInt(partes[1]);
            Base64.Decoder base64 = Base64.getDecoder();
            byte[] sal = base64.decode(partes[2]);
            byte[] esperado = base64.decode(partes[3]);
            byte[] calculado = calcular(clave.toCharArray(), sal, iteraciones);
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] calcular(char[] clave, byte[] sal, int iteraciones) {
        PBEKeySpec spec = new PBEKeySpec(clave, sal, iteraciones, BITS_HASH);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 no está disponible en este JDK.", e);
        } finally {
            spec.clearPassword();
        }
    }
}
