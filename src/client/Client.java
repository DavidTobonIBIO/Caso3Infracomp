package client;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {

    public static final int PORT = 3400;
    public static final String HOST = "localhost";

    public void launchWithConsole() {
        Scanner scanner = new Scanner(System.in);
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("Bienvenido al CLIENTE, seleccione una de las opciones: \n1. Ejecutar \n0. Salir");
            int option = scanner.nextInt();
            if (option == 1) {
                System.out.println("Inicializando Cliente...");
                start();
                inMenu = false;
            } else if (option == 0) {
                System.out.println("Gracias por usar el sistema");
                inMenu = false;
            } else {
                System.out.println("Opción no válida");
            }
        }
        scanner.close();
    }

    @Override
    public void run() {
        Socket socket = null;
        PrintWriter writer = null;
        BufferedReader reader = null;
        boolean continueFlag = true;
        while (continueFlag) {
            try {
                System.out.println("Conectando al servidor...");
                socket = new Socket(HOST, PORT);
                writer = new PrintWriter(socket.getOutputStream(), true);
                reader = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));

                ClientProtocol.execute(reader, writer);

                reader.close();
                writer.close();
                socket.close();
                continueFlag = false;
            } catch (Exception e) {
                System.out.println("Error al conectar con el servidor");
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
}
