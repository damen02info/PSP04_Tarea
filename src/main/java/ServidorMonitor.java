import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * ServidorMonitor
 *
 * @author Daniel Mendez Arias - 13/01/2026
 * Programa que implementa un servidor TCP que escucha en el puerto 9000 y responde a comandos específicos.
 */

public class ServidorMonitor {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9000);
        System.out.println("ServidorMonitor escuchando en el puerto 9000...");

        while (true) {
            Socket socCliente = serverSocket.accept();
            // Se crea y lanza un nuevo hilo para cada cliente
            new Thread(new ManejadorCliente(socCliente)).start();
        }
    }
}

class ManejadorCliente implements Runnable {
    private final Socket socketCliente;

    // Constructor para inicializar el socket del cliente
    public ManejadorCliente(Socket socket) {
        this.socketCliente = socket;
    }

    @Override
    public void run() {
        long tiempoInicio = System.currentTimeMillis();
        System.out.println("Conexión aceptada de " + socketCliente.getInetAddress() + ":" + socketCliente.getPort());

        try (
                BufferedReader bufLeer = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
                PrintWriter printWriter = new PrintWriter(socketCliente.getOutputStream(), true)
        ) {
            // Logica de petición y respuesta
            String peticion = bufLeer.readLine();
            if (peticion == null) return;
            System.out.println("Petición recibida: " + peticion);
            peticion = peticion.toUpperCase();
            String cuerpoHtml = "<html><body><h1>Comando no reconocido</h1></body></html>";

            if (peticion.contains("HORA")) {
                String fechaHora = LocalDateTime.now().toString();
                cuerpoHtml = "<html><body><h1>Hora actual</h1><p>" + fechaHora + "</p></body></html>";
                System.out.println("Respondido con la hora actual: " + fechaHora);
            } else if (peticion.contains("IP")) {
                String ipCliente = socketCliente.getInetAddress().toString();
                int puertoRemoto = socketCliente.getPort();
                cuerpoHtml = "<html><body><h1>IP y Puerto</h1><p>IP: " + ipCliente + "<br>Puerto: " + puertoRemoto + "</p></body></html>";
                System.out.println("Respondido con IP y puerto: " + ipCliente + ", " + puertoRemoto);
            } else if (peticion.contains("FIN")) {
                cuerpoHtml = "<html><body><h1>Conexión cerrada</h1></body></html>";
                printWriter.println("HTTP/1.1 200 OK");
                printWriter.println("Content-Type: text/html; charset=utf-8");
                printWriter.println("Content-Length: " + cuerpoHtml.getBytes(StandardCharsets.UTF_8).length);
                printWriter.println();
                printWriter.println(cuerpoHtml);
                System.out.println("Conexión cerrada por petición del cliente.");
                socketCliente.close();
                long tiempoFin = System.currentTimeMillis();
                System.out.println("Conexión con " + socketCliente.getInetAddress().getHostAddress() + " cerrada. Duración: " + (tiempoFin - tiempoInicio) + " ms.");

            }

            // Cabeceras y demas para enviar la respuesta HTTP
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type: text/html; charset=utf-8");
            printWriter.println("Content-Length: " + cuerpoHtml.getBytes(StandardCharsets.UTF_8).length);
            printWriter.println();
            printWriter.println(cuerpoHtml);
            printWriter.flush();

        } catch (IOException e) {
            System.err.println("Error de comunicación con el cliente: " + e.getMessage());
        }
    }
}
