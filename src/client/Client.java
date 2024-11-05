package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {

    private static final int PORT = 3400;
    private static final String HOST = "localhost";

    private int id;

    public Client(int id) {
        this.id = id;
    }

    public void launchWithConsole() {
        Scanner scanner = new Scanner(System.in);
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("Bienvenido al CLIENTE, seleccione una de las opciones: \n1. Ejecutar cliente iterativo\n2. Ejecutar cliente concurrente\n0. Salir");
            int option = scanner.nextInt();
            if (option == 1) {
                System.out.println("Inicializando Cliente Iterativo...");
                startIterativeClient();
                inMenu = false;
            } else if (option == 2) {
                System.out.println("Inicializando Clientes Concurrentes...");
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
        
        try {
            System.out.println("Conectando al servidor...");
            socket = new Socket(HOST, PORT);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            ClientProtocol.execute(reader, writer, false);

            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error al conectar con el servidor");
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("Desconectado del servidor");
    }

    public void startIterativeClient() {
        Socket socket = null;
        PrintWriter writer = null;
        BufferedReader reader = null;

        try {
            System.out.println("Conectando al servidor...");
            socket = new Socket(HOST, PORT);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            ClientProtocol.execute(reader, writer, true);

            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error al conectar con el servidor");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public int getClientId() {
        return id;
    }
}
