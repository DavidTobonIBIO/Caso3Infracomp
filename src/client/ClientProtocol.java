package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import asymmetric.Asymmetric;
import symmetric.Symmetric;

public class ClientProtocol {

    private static final int NUM_ITERATIONS = 32;
    private static PublicKey publicKey;
    private static SecretKey symmetricKey;

    public static void loadKeys() {
        loadKey("RSA");
        loadKey("AES");
    }

    public static void execute(BufferedReader reader, PrintWriter writer, boolean isIterative) throws IOException {
        if (isIterative) {
            runIterativeCommunication(reader, writer);
        } else {
            runConcurrentCommunication(reader, writer);
        }
    }

    private static void startCommunication(PrintWriter writer) {
        writer.println("SECINIT");
    }

    private static void endCommunication(PrintWriter writer) {
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
            System.out.println("Error al cargar las llaves");
            e.printStackTrace();
            System.exit(-1);
        }        
    }

    private static void runIterativeCommunication(BufferedReader reader, PrintWriter writer) throws IOException {
        startCommunication(writer);
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            // TODO: implementar la parte del cliente iterativo que se repite 32 veces
            System.out.println("Iteración " + i);
        }
        endCommunication(writer);
    }

    private static void runConcurrentCommunication(BufferedReader reader, PrintWriter writer) throws IOException {
        startCommunication(writer);
        endCommunication(writer);
    }
    
}
