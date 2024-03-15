import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class ServidorUDP {
    private static final int PUERTO = 12345;

    public static void main(String[] args) {
        DatagramSocket socket = null; // Declarar la variable socket fuera del bloque try
        try {
            // Crear el socket del servidor y mostrar un mensaje de espera
            socket = new DatagramSocket(PUERTO);
            System.out.println("Servidor en espera");

            // Mapa para almacenar los clientes conectados y sus mensajes
            Map<InetSocketAddress, SecretKey> clientesConectados = new HashMap<>();

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                // Esperar a recibir un mensaje de un cliente
                socket.receive(packet);
                String mensajeCifrado = new String(packet.getData(), 0, packet.getLength());
                // Obtener la dirección y el puerto del cliente
                InetSocketAddress cliente = new InetSocketAddress(packet.getAddress(), packet.getPort());

                // Verificar si el cliente es nuevo
                if (!clientesConectados.containsKey(cliente)) {
                    // Si el cliente es nuevo, recibir la clave AES del cliente y agregarlo al mapa
                    SecretKey claveAES = recibirClave(socket, mensajeCifrado);
                    clientesConectados.put(cliente, claveAES);
                    System.out.println("Nuevo cliente registrado");
                } else {
                    // Si el cliente ya está registrado, descifrar el mensaje recibido
                    SecretKey claveAES = clientesConectados.get(cliente);
                    String mensajeDescifrado = Encriptacion.desencriptar(mensajeCifrado, claveAES);
                    System.out.println("Mensaje recibido: " + mensajeDescifrado);
                }
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            // Capturar excepciones de entrada/salida
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } finally {
            // Cerrar el socket al finalizar la ejecución del servidor
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    // Método para recibir la clave AES del cliente
    private static SecretKey recibirClave(DatagramSocket socket, String mensajeCifrado) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] claveBytes = mensajeCifrado.getBytes();
        return new SecretKeySpec(claveBytes, 0, claveBytes.length, "AES");
    }
}
