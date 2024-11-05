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
import java.util.Random;

import javax.crypto.SecretKey;

import SHA.SHA1RSA;
import SHA.SHA512;
import asymmetric.Asymmetric;
import symmetric.Symmetric;
import java.math.BigInteger;

public class ClientProtocol {

    private static final int NUM_ITERATIONS = 32;
    private PublicKey publicKey;
    private SecretKey symmetricKey;
    private String P;
    private String G;
    private String Y;
    private BigInteger YClient;
    private BigInteger x;
    private SecretKey K_AB1;
    private SecretKey K_AB2;
    private BigInteger Reto;

    public  void loadKeys() {
        loadKey("RSA");
        loadKey("AES");
    }

    public  void execute(BufferedReader reader, PrintWriter writer, boolean isIterative) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException {
        if (isIterative) {
            runIterativeCommunication(reader, writer);
        } else {
            runConcurrentCommunication(reader, writer);
        }
    }

    private  void startCommunication(PrintWriter writer) {
        writer.println("SECINIT");
        generateChallenge();
        cipherChallenge(writer);
        writer.println("OK Reto");
    }

    private  void endCommunication(PrintWriter writer) {
        writer.println("TERMINAR");
    }

    private  void loadKey(String algorithm) {
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

    private  void runIterativeCommunication(BufferedReader reader, PrintWriter writer) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException {
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

    private  void runConcurrentCommunication(BufferedReader reader, PrintWriter writer) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException {
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
    

    private  byte[] diffie(PrintWriter writer, BufferedReader reader) throws IOException{
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

    private  void createY(){
        BigInteger[] YX = Symmetric.generateY(new BigInteger(P), Integer.parseInt(G));
        YClient = YX[0];
        x = YX[1];
    }
    private  boolean checkSignature(byte[] signature) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException{
        String message = P + G + Y;
        boolean check = SHA1RSA.verify( message, publicKey, signature);
        return check;
    }

    public  void getMasterKey(PrintWriter writer, BufferedReader reader) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
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

    public  void generateChallenge(){
        Random rand = new Random();
        Reto = new BigInteger(1024, rand);
    }

    public  void cipherChallenge(PrintWriter writer){
        String reto = String.valueOf(Reto);
        byte[] cifrado = Asymmetric.cipher(K_AB1,"RSA", reto);
        String encodedK_AB1 = Base64.getEncoder().encodeToString(cifrado);
        writer.println(encodedK_AB1);
    }
}
