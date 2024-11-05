package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {

    private static final int PORT = 3400;
    private static final String HOST = "localhost";

    private int id;

    public Client(int id) {
        this.id = id;
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
