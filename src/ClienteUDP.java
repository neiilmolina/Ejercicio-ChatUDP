import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

            // Enviar un mensaje al servidor informando la entrada del cliente al chat
            enviarMensaje(socket, nombreCliente, direccionServidor, PUERTO_SERVIDOR);

            // Crear un hilo para recibir mensajes del servidor
            Thread recibirMensajes = new Thread(() -> {
                try {
                    while (true) {
                        // Método para recibir mensajes del servidor
                        recibirMensaje(socket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            recibirMensajes.start();

            // Bucle para que el cliente pueda enviar mensajes al servidor
            while (true) {
                // Solicitar al usuario que ingrese un mensaje
                System.out.print("Mensaje: ");
                String message = scanner.nextLine();
                // Obtener la fecha y hora actual
                LocalDateTime fechaHoraActual = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String fechaHoraFormateada = fechaHoraActual.format(formatter);
                // Enviar el mensaje al servidor incluyendo la fecha y el nombre del cliente
                enviarMensaje(socket, nombreCliente + ": " + message +"[" + fechaHoraFormateada + "]", direccionServidor, PUERTO_SERVIDOR);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para enviar un mensaje al servidor
    private static void enviarMensaje(DatagramSocket socket, String mensaje, InetAddress direccion, int puerto) throws IOException {
        byte[] sendData = mensaje.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, direccion, puerto);
        socket.send(sendPacket);
    }

    // Método para recibir un mensaje del servidor
    private static void recibirMensaje(DatagramSocket socket) throws IOException {
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket packetRecibido = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(packetRecibido);
        String mensajeRecibido = new String(packetRecibido.getData(), 0, packetRecibido.getLength());
        System.out.println(mensajeRecibido);
    }
}