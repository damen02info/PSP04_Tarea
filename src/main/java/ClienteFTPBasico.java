import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClienteFTPBasico {

    public static void main(String[] args) {
        FTPClient clienteFTP = new FTPClient();
        try {

            // Conectar al servidor FTP
            clienteFTP.connect("192.168.1.129", 2221);

            if (clienteFTP.login("dani", "1234")) {
                System.out.println("Conexión y autenticación exitosa.");
                // Configurar el modo de transferencia a binario para evitar problemas con archivos no textuales
                clienteFTP.setFileType(FTP.BINARY_FILE_TYPE);

                String[] archivos = clienteFTP.listNames();
                if (archivos == null || archivos.length == 0) {
                    System.out.println("No se encontraron archivos en el directorio.");
                    return;
                }

                listarArchivos(archivos);
                descargarArchivoSeleccionado(clienteFTP, archivos);

            } else {
                System.out.println("Error de autenticación.");
            }
        } catch (IOException e) {
            System.err.println("Error de conexión o de E/S: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ha ocurrido un error inesperado: " + e.getMessage());
        } finally {
            desconectarFTP(clienteFTP);
        }
    }

    private static void listarArchivos(String[] archivos) {
        System.out.println("Archivos en el directorio raíz:");
        for (int i = 0; i < archivos.length; i++) {
            System.out.println(i + ": " + archivos[i]);
        }
    }

    private static void descargarArchivoSeleccionado(FTPClient clienteFTP, String[] archivos) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Introduce el índice del archivo a descargar: ");

        try {
            int indice = sc.nextInt();
            if (indice < 0 || indice >= archivos.length) {
                System.out.println("Índice inválido.");
                return;
            }

            String archivoADescargar = archivos[indice];
            System.out.println("Descargando archivo: " + archivoADescargar);

            // Descargar el archivo seleccionado mediante un FileOutputStream
            try (FileOutputStream fos = new FileOutputStream(archivoADescargar)) {
                if (clienteFTP.retrieveFile(archivoADescargar, fos)) {
                    System.out.println("Archivo [" + archivoADescargar + "] descargado exitosamente.");
                } else {
                    System.out.println("Error al descargar el archivo: " + archivoADescargar);
                }
            }

        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Debes introducir un número.");
        } catch (IOException e) {
            System.out.println("Error de E/S al escribir el archivo local: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ha ocurrido un error inesperado: " + e.getMessage());
        }
    }

    private static void desconectarFTP(FTPClient clienteFTP) {
        if (clienteFTP != null && clienteFTP.isConnected()) {
            try {
                clienteFTP.logout();
                clienteFTP.disconnect();
                System.out.println("Desconectado del servidor FTP.");
            } catch (IOException ex) {
                System.err.println("Error al desconectar del servidor FTP: " + ex.getMessage());
            }
        }
    }
}
