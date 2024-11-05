package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKey;

import SHA.SHA1RSA;
import SHA.SHA512;
import asymmetric.Asymmetric;
import symmetric.Symmetric;
import java.math.BigInteger;

public class ClientProtocol {

    private static final int NUM_ITERATIONS = 32;
    private static PublicKey publicKey;
    private static SecretKey symmetricKey;
    private static String P;
    private static String G;
    private static String Y;
    private static BigInteger YClient;
    private static BigInteger x;
    private static SecretKey K_AB1;
    private static SecretKey K_AB2;

    public static void loadKeys() {
        loadKey("RSA");
        loadKey("AES");
    }

    public static void execute(BufferedReader reader, PrintWriter writer, boolean isIterative) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException {
        if (isIterative) {
            runIterativeCommunication(reader, writer);
        } else {
            runConcurrentCommunication(reader, writer);
        }
    }

    private static void startCommunication(PrintWriter writer) {
        writer.println("SECINIT");
        //TODO: Agregar pasos 2-6
        writer.println("OK Reto");
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

    private static void runIterativeCommunication(BufferedReader reader, PrintWriter writer) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException {
        startCommunication(writer);
        byte[] firma = diffie(writer, reader);
        boolean check = checkSignature(firma);
        if (check){
            writer.println("OK Diffie-Hellman");
            createY();
            writer.println(String.valueOf(YClient));
            getMasterKey(writer, reader);
            for (int i = 0; i < NUM_ITERATIONS; i++) {
                // TODO: implementar la parte del cliente iterativo que se repite 32 veces
                System.out.println("Iteración " + i);
            }
        }else{
            writer.println("ERROR");
        }
        
        endCommunication(writer);
    }

    private static void runConcurrentCommunication(BufferedReader reader, PrintWriter writer) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException {
        startCommunication(writer);
        byte[] firma = diffie(writer, reader);
        boolean check = checkSignature(firma);
        if (check){
            writer.println("OK Diffie-Hellman");
            createY();
            writer.println(String.valueOf(YClient));
            getMasterKey(writer, reader);
        }else{
            writer.println("ERROR");
        }
        endCommunication(writer);
    }
    

    private static byte[] diffie(PrintWriter writer, BufferedReader reader) throws IOException{
        //writer.println("diffie");
        G = reader.readLine();
        System.out.println("G: " + G);
        P = reader.readLine();
        System.out.println("P: " + P);
        Y = reader.readLine();
        System.out.println("Y: " + Y);
        String firma = reader.readLine();
        byte[] firmaByte = Base64.getDecoder().decode(firma);
        
        return firmaByte;
    }

    private static void createY(){
        BigInteger[] YX = Symmetric.generateY(new BigInteger(P), Integer.parseInt(G));
        YClient = YX[0];
        x = YX[1];
    }
    private static boolean checkSignature(byte[] signature) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException{
        String message = P + G + Y;
        boolean check = SHA1RSA.verify( message, publicKey, signature);
        return check;
    }

    public static void getMasterKey(PrintWriter writer, BufferedReader reader) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
        BigInteger YServer = new BigInteger(Y);
        BigInteger master = YServer.modPow(x, new BigInteger(P));
        SecretKey[] masterKeys = SHA512.encrypt(String.valueOf(master));
        K_AB1 = masterKeys[0];
        K_AB2 = masterKeys[1];
        String encodedK_AB1 = Base64.getEncoder().encodeToString(K_AB1.getEncoded());
        String encodedK_AB2 = Base64.getEncoder().encodeToString(K_AB2.getEncoded());
        System.out.println(encodedK_AB1);
        System.out.println(encodedK_AB2); 
        //System.out.println(String.valueOf(master));
    }
}
