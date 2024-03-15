import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Encriptacion {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_CIPHER = "AES/ECB/PKCS5Padding"; // Modo ECB con relleno PKCS5

    // Método para generar una clave secreta AES
    public static SecretKey generarClaveAES() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(256); // Tamaño de la clave: 128 bits
        return keyGenerator.generateKey();
    }

    // Método para cifrar un mensaje usando AES
    public static String encriptar(String mensaje, SecretKey clave) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(AES_CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, clave);
        byte[] mensajeCifrado = cipher.doFinal(mensaje.getBytes());
        return Base64.getEncoder().encodeToString(mensajeCifrado);
    }

    // Método para descifrar un mensaje usando AES
    public static String desencriptar(String mensajeCifrado, SecretKey clave) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(AES_CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, clave);
        byte[] bytesDecodificados = Base64.getDecoder().decode(mensajeCifrado);
        byte[] mensajeDescifrado = cipher.doFinal(bytesDecodificados);
        return new String(mensajeDescifrado);
    }
}
