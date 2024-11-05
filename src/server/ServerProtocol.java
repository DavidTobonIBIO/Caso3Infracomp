package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKey;

import SHA.SHA1RSA;
import SHA.SHA512;
import asymmetric.Asymmetric;
import symmetric.Symmetric;

public class ServerProtocol {

    //private static BigInteger Y;
    //private static BigInteger P;
    //private static int G;
    //private static BigInteger x;

    private static final String OPEN_SSL_PATH = "OpenSSL-1.1.1h_win32";
    private static SecretKey K_AB1;
    private static SecretKey K_AB2;

    public static boolean execute(BufferedReader reader, PrintWriter writer) throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, SignatureException, InvalidKeySpecException {
        String inputLine = reader.readLine();
        if (inputLine.equals("SECINIT"))  {
            System.out.println("Cliente ha iniciado comunicacion segura");
            String reto = reader.readLine();
            System.out.println("reto:" + reto);
            String rta = getReto(reto);
            System.out.println("reto: " + rta);
            writer.println(rta);
            inputLine = reader.readLine();
            if (inputLine.equals("OK")) {
                System.out.println("Cliente ha respondido correctamente al reto");
                String[] GPYX = diffieHellman();
                String G = GPYX[0];
                String P = GPYX[1];
                String Y = GPYX[2];
                String x = GPYX[3];
                writer.println(G);
                System.out.println(G);
                writer.println(P);
                System.out.println(P);
                writer.println(Y);
                System.out.println(Y);
                String firma = sign(P, Y, G);
                writer.println(firma);
                inputLine = reader.readLine();
                if (inputLine.equals("OK")) {
                    System.out.println("Cliente ha verificado la firma");
                    BigInteger pBig = new BigInteger(P);
                    BigInteger xBig = new BigInteger(x);
                    getMasterKey(writer, reader, xBig, pBig);
                    // TODO: HMAC
                    getPackageRequest(reader, writer);
                    inputLine = reader.readLine();
                    if (inputLine.equals("TERMINAR")) {
                        System.out.println("Cliente ha solicitado desconexion.");
                        writer.println("Desconexion exitosa");
                    }
                } else {
                    System.out.println("Falla en la verificacion de la firma");
                }
            } else {
                System.out.println("Cliente no ha respondido correctamente al reto");
            }
        }
        return false;
    }

    private static String[] diffieHellman()
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        try {
            String[] GP = Symmetric.generatePG(OPEN_SSL_PATH);
            System.out.println("Llaves simetricas generadas");
            int G = Integer.parseInt(GP[1]);
            BigInteger P = Symmetric.parser(GP[0]);
            //writer.println(String.valueOf(G));
            //writer.println(String.valueOf(P));
            BigInteger[] YX = Symmetric.generateY(P, G);
            BigInteger Y = YX[0];
            BigInteger x = YX[1];
            //writer.println(String.valueOf(Y));
            //sign(P, Y, G, writer);
            String [] GPYX = new String[] {String.valueOf(G), String.valueOf(P), String.valueOf(Y), String.valueOf(x)};
            return GPYX;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static PrivateKey getPrivateKey() {
        PrivateKey privateKey = Server.getPrivateKey();
        return privateKey;
    }

    private static String sign(String P, String Y, String G)
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {

        String message = P + G + Y;
        byte[] firma = SHA1RSA.sign(getPrivateKey(), message);

        String firmaString = Base64.getEncoder().encodeToString(firma);
        return firmaString;
    }

    public static void getMasterKey(PrintWriter writer, BufferedReader reader, BigInteger x, BigInteger P)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String YClient = reader.readLine();
        BigInteger YClient_int = new BigInteger(YClient);
        BigInteger master = YClient_int.modPow(x, P);
        SecretKey[] masterKeys = SHA512.encrypt(String.valueOf(master));
        K_AB1 = masterKeys[0];
        K_AB2 = masterKeys[1];
        String encodedK_AB1 = Base64.getEncoder().encodeToString(K_AB1.getEncoded());
        String encodedK_AB2 = Base64.getEncoder().encodeToString(K_AB2.getEncoded());
        System.out.println(encodedK_AB1);
        System.out.println(encodedK_AB2);
    }

    public static String getReto(String reto) {
        byte[] retoByte = Base64.getDecoder().decode(reto);
        PrivateKey privateKey = getPrivateKey();
        byte[] rta = Asymmetric.decipher(privateKey, "RSA", retoByte);
        String reto_decode = Base64.getEncoder().encodeToString(rta);
        //System.out.println(encodedK_AB1);
        return reto_decode;
    }

    public static void getPackageRequest(BufferedReader reader, PrintWriter writer) throws IOException {
        String encryptedClientId = reader.readLine();
        String hmacClientId = reader.readLine();
        String encryptedPackageId = reader.readLine();
        String hmacPackageId = reader.readLine();

        System.out.println("C(K_AB1, uid): " + encryptedClientId);
        System.out.println("HMAC(K_AB2, uid): " + hmacClientId);
        System.out.println("C(K_AB1, paquete_id): " + encryptedPackageId);
        System.out.println("HMAC(K_AB2, paquete_id): " + hmacPackageId);
        
    }
}
