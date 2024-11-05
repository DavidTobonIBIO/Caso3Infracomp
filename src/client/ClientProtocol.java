package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Base64;

import javax.crypto.SecretKey;

import SHA1RSA.SHA1RSA;
import asymmetric.Asymmetric;
import symmetric.Symmetric;
import java.math.BigInteger;

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
    

    private static void diffie(PrintWriter writer, BufferedReader reader) throws IOException{
        writer.println("diffie");
        String G = reader.readLine();
        System.out.println("G: " + G);
        String P = reader.readLine();
        System.out.println("P: " + P);
        String Y = reader.readLine();
        System.out.println("Y: " + Y);
        String firma = reader.readLine();
        byte[] firmaByte = Base64.getDecoder().decode(firma);
        String message = P + G + Y;
        
        try {
            boolean check = checkSignature(firmaByte, message);
            System.out.println(check);
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SignatureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        BigInteger YClient = Symmetric.generateY(new BigInteger(P), Integer.parseInt(G));
        //writer.println(String.valueOf(YClient));
    }

    private static boolean checkSignature(byte[] signature, String message) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException{
        boolean check = SHA1RSA.verify( message, publicKey, signature);
        return check;
    }
}
