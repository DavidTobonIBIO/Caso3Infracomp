package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread {
    
    private Socket clientSocket = null;
    private int id;

    public ServerThread(Socket clientSocket, int id) {
        this.clientSocket = clientSocket;
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Iniciando hilo " + id + " para cliente " + clientSocket.getInetAddress().getHostAddress());
        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(clientSocket.getInputStream()));
            ServerProtocol.execute(reader, writer);
            
            writer.close();
            reader.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error al iniciar el hilo " + id);
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
