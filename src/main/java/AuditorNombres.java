import java.net.InetAddress;
import java.net.URL;
import java.util.Scanner;

/**
 * AuditorNombres
 * @author Daniel Mendez Arias - 13/01/2026
 * Programa que solicita una dirección web al usuario y realiza las siguientes tareas:
 */

public class AuditorNombres {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Introduce una dirección web: ");
        String web = sc.nextLine();

        try {
            // Resolución de nombres
            InetAddress direccionWeb = InetAddress.getByName(web);
            System.out.println("Dirección IP: " + direccionWeb.getHostAddress());
            System.out.println("Nombre canónico del host: " + direccionWeb.getCanonicalHostName());

            // Análisis de URL
            URL url = new URL("http://" + direccionWeb);
            System.out.println("Protocolo: " + url.getProtocol());
            System.out.println("Host: " + url.getHost());
            int puerto = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
            System.out.println("Puerto: " + puerto);

            // Información de Cabecera
            var conexion = url.openConnection();
            System.out.println("Tipo de contenido: " + conexion.getContentType());
            System.out.println("Tamaño del contenido: " + conexion.getContentLength() + " bytes");
            System.out.println("Fecha de la última modificación: " + conexion.getLastModified());
        } catch (Exception e) {
            System.out.println("El host no es alcanzable.");
        }
    }
}
