import java.io.IOException;
import java.net.*;
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
            Map<InetSocketAddress, String> clientesConectados = new HashMap<>();

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                // Esperar a recibir un mensaje de un cliente
                socket.receive(packet);
                String mensaje = new String(packet.getData(), 0, packet.getLength());
                // Obtener la dirección y el puerto del cliente
                InetSocketAddress cliente = new InetSocketAddress(packet.getAddress(), packet.getPort());

                // Verificar si el cliente es nuevo
                if (!clientesConectados.containsKey(cliente)) {
                    // Si el cliente es nuevo, agregarlo al mapa y enviar un mensaje de bienvenida a todos los clientes
                    clientesConectados.put(cliente, mensaje);
                    System.out.println("Nuevo cliente registrado: " + mensaje);
                    // Notificar a todos los clientes sobre la entrada del nuevo cliente
                    mensaje = "¡" + mensaje + " ha entrado al chat!";
                    enviarMensajeCliente(socket, mensaje, clientesConectados);
                } else {
                    // Si el cliente ya está registrado, retransmitir el mensaje a todos los clientes
                    String mensajeCliente = clientesConectados.get(cliente);
                    enviarMensajeCliente(socket, mensaje, clientesConectados);
                }
            }
        } catch (IOException e) {
            // Capturar excepciones de entrada/salida
            e.printStackTrace();
        } finally {
            // Cerrar el socket al finalizar la ejecución del servidor
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    // Método para retransmitir un mensaje a todos los clientes
    private static void enviarMensajeCliente(DatagramSocket socket, String mensaje, Map<InetSocketAddress, String> clientesConectados) throws IOException {
        byte[] sendData = mensaje.getBytes();
        for (InetSocketAddress cliente : clientesConectados.keySet()) {
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, cliente.getAddress(), cliente.getPort());
                socket.send(sendPacket);
            } catch (SocketException e) {
                // Cliente desconectado, eliminarlo de la lista y notificar a los demás clientes
                String mensajeDesconexion = "¡" + clientesConectados.get(cliente) + " se ha desconectado!";
                System.out.println(mensajeDesconexion);
                clientesConectados.remove(cliente);
                enviarMensajeCliente(socket, mensajeDesconexion, clientesConectados);
            }
        }
    }
}
