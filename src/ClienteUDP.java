import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Scanner;

public class ClienteUDP {
    private static final String DIRECCION_SERVIDOR = "localhost";
    private static final int PUERTO_SERVIDOR = 12345;

    public static void main(String[] args) {
        try {
            // Creación del socket para comunicarse con el servidor
            DatagramSocket socket = new DatagramSocket();
            InetAddress direccionServidor = InetAddress.getByName(DIRECCION_SERVIDOR);
            Scanner scanner = new Scanner(System.in);

            // Solicitar al usuario que ingrese su nombre
            System.out.print("Ingrese su nombre: ");
            String nombreCliente = scanner.nextLine();

            // Generar una clave AES
            SecretKey claveAES = Encriptacion.generarClaveAES();

            // Enviar la clave al servidor
            enviarClave(socket, claveAES, direccionServidor, PUERTO_SERVIDOR);

            // Crear un hilo para recibir mensajes del servidor
            Thread recibirMensajes = new Thread(() -> {
                try {
                    while (true) {
                        // Método para recibir mensajes del servidor
                        recibirMensaje(socket, claveAES);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    throw new RuntimeException(e);
                } catch (IllegalBlockSizeException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (BadPaddingException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeyException e) {
                    throw new RuntimeException(e);
                }
            });
            recibirMensajes.start();

            // Bucle para que el cliente pueda enviar mensajes al servidor
            while (true) {
                // Solicitar al usuario que ingrese un mensaje
                System.out.print("Mensaje: ");
                String message = scanner.nextLine();
                // Obtener la fecha y hora actual
                String fechaHoraFormateada = obtenerFechaHoraFormateada();
                // Cifrar el mensaje antes de enviarlo
                String mensajeCifrado = Encriptacion.encriptar(nombreCliente + ": " + message + " [" + fechaHoraFormateada + "]", claveAES);
                // Enviar el mensaje cifrado al servidor
                enviarMensaje(socket, mensajeCifrado, direccionServidor, PUERTO_SERVIDOR);
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    // Método para enviar la clave al servidor
    private static void enviarClave(DatagramSocket socket, SecretKey clave, InetAddress direccion, int puerto) throws IOException {
        byte[] claveBytes = clave.getEncoded();
        DatagramPacket sendPacket = new DatagramPacket(claveBytes, claveBytes.length, direccion, puerto);
        socket.send(sendPacket);
    }

    // Método para obtener la fecha y hora actual formateada
    private static String obtenerFechaHoraFormateada() {
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return fechaHoraActual.format(formatter);
    }

    // Método para enviar un mensaje al servidor
    private static void enviarMensaje(DatagramSocket socket, String mensaje, InetAddress direccion, int puerto) throws IOException {
        byte[] sendData = mensaje.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, direccion, puerto);
        socket.send(sendPacket);
    }

    // Método para recibir un mensaje del servidor y desencriptarlo
    private static void recibirMensaje(DatagramSocket socket, SecretKey claveAES) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket packetRecibido = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(packetRecibido);
        String mensajeRecibido = new String(packetRecibido.getData(), 0, packetRecibido.getLength());
        // Desencriptar el mensaje recibido
        String mensajeDesencriptado = Encriptacion.desencriptar(mensajeRecibido, claveAES);
        System.out.println(mensajeDesencriptado);
    }
}
