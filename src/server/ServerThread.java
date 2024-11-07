package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.io.InputStreamReader;

public class ServerThread extends Thread {

    private Socket clientSocket = null;
    private int id;
    private ServerProtocol serverProtocol;

    public ServerThread(Socket clientSocket, int id, ServerProtocol serverProtocol) {
        this.clientSocket = clientSocket;
        this.id = id;
        this.serverProtocol = serverProtocol;
    }

    @Override
    public void run() {
        System.out.println("Iniciando hilo " + id + " para cliente " + clientSocket.getInetAddress().getHostAddress());
        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            serverProtocol.execute(reader, writer);

            writer.close();
            reader.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error al iniciar el hilo " + id);
            e.printStackTrace();
            System.exit(-1);
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
}
