package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import asymmetric.Asymmetric;
import symmetric.Symmetric;
import java.math.BigInteger;

import symmetric.Symmetric;

public class ClientProtocol {

    private static PublicKey publicKey;
    private static SecretKey symmetricKey;

    public static void loadKeys() {
        loadKey("RSA");
        loadKey("AES");
    }

    public static void execute(BufferedReader reader, PrintWriter writer) throws IOException {
        startCommunication(writer);
        diffie(writer, reader);
        disconnect(writer);
    }

    private static void startCommunication(PrintWriter writer) {
        writer.println("SECINIT");
    }

    private static void disconnect(PrintWriter writer) {
        writer.println("TERMINAR");
    }

    private static void loadKey(String algorithm) {
        try {
            if (algorithm.equals("RSA")) {
                publicKey = Asymmetric.loadPublicKey(algorithm);
            } else if (algorithm.equals("AES")) {
                symmetricKey = Symmetric.loadKey(algorithm);
            }
        } catch (Exception e) {
            System.out.println("Error al cargar llave publica");
            e.printStackTrace();
            System.exit(-1);
        }        
    }

    private static void diffie(PrintWriter writer, BufferedReader reader) throws IOException{
        writer.println("diffie");
        String G = reader.readLine();
        System.out.println("G: " + G);
        String P = reader.readLine();
        System.out.println("P: " + P);
        String Y = reader.readLine();
        System.out.println("Y: " + Y);

        BigInteger YClient = Symmetric.generateY(new BigInteger(P), Integer.parseInt(G));
        writer.println(String.valueOf(YClient));
    }
}
