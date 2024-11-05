package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public class Client extends Thread {

    private static final int PORT = 3400;
    private static final String HOST = "localhost";
    private ClientProtocol clientProtocol = new ClientProtocol();

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
            
            try {
                clientProtocol.loadKeys();
                clientProtocol.execute(reader, writer, false);
            } catch (InvalidKeyException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SignatureException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

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

            try {
                clientProtocol.execute(reader, writer, true);
            } catch (InvalidKeyException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SignatureException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

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
