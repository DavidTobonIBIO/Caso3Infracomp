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

    private static BigInteger Y;
    private static BigInteger P;
    private static int G;
    private static BigInteger x;

    private static final String OPEN_SSL_PATH = "OpenSSL-1.1.1h_win32";
    private static SecretKey K_AB1;
    private static SecretKey K_AB2;

    public static boolean execute(BufferedReader reader, PrintWriter writer) throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, SignatureException, InvalidKeySpecException {
        String inputLine = reader.readLine();
        if (inputLine.equals("SECINIT"))  {
            System.out.println("Cliente ha iniciado comunicacion segura");
            getReto(reader, writer);
            inputLine = reader.readLine();
            if (inputLine.equals("OK")) {
                System.out.println("Cliente ha respondido correctamente al reto");
                diffieHellman(writer);
                inputLine = reader.readLine();
                if (inputLine.equals("OK")) {
                    System.out.println("Cliente ha verificado la firma");
                    getMasterKey(writer, reader);
                    // TODO: HMAC
                    inputLine = getPackageRequest(reader, writer);
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

    private static void diffieHellman(PrintWriter writer)
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        try {
            String[] GP = Symmetric.generatePG(OPEN_SSL_PATH);
            System.out.println("Llaves simetricas generadas");
            G = Integer.parseInt(GP[1]);
            P = Symmetric.parser(GP[0]);
            writer.println(String.valueOf(G));
            writer.println(String.valueOf(P));
            BigInteger[] YX = Symmetric.generateY(P, G);
            Y = YX[0];
            x = YX[1];
            writer.println(String.valueOf(Y));
            sign(P, Y, G, writer);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static PrivateKey getPrivateKey() {
        PrivateKey privateKey = Server.getPrivateKey();
        return privateKey;
    }

    private static void sign(BigInteger P, BigInteger Y, int G, PrintWriter writer)
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        String pString = String.valueOf(P);
        String gString = String.valueOf(G);
        String yString = String.valueOf(Y);

        String message = pString + gString + yString;
        byte[] firma = SHA1RSA.sign(getPrivateKey(), message);

        String firmaString = Base64.getEncoder().encodeToString(firma);
        writer.println(firmaString);
    }

    public static void getMasterKey(PrintWriter writer, BufferedReader reader)
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

    public static void getReto(BufferedReader reader, PrintWriter writer) throws IOException {
        String reto = reader.readLine();;
        PrivateKey privateKey = getPrivateKey();
        byte[] rta = Asymmetric.decipher(privateKey, "RSA", reto);
        String encodedK_AB1 = Base64.getEncoder().encodeToString(rta);
        System.out.println(encodedK_AB1);
    }

    public static String getPackageRequest(BufferedReader reader, PrintWriter writer) throws IOException {
        String inputLine = reader.readLine();
        while (!inputLine.equals("TERMINAR")) {
            String encryptedClientId = inputLine;
            String hmacClientId = reader.readLine();
            String encryptedPackageId = reader.readLine();
            String hmacPackageId = reader.readLine();

            System.out.println("C(K_AB1, uid): " + encryptedClientId);
            System.out.println("HMAC(K_AB2, uid): " + hmacClientId);
            System.out.println("C(K_AB1, paquete_id): " + encryptedPackageId);
            System.out.println("HMAC(K_AB2, paquete_id): " + hmacPackageId);

            String decryptedClientId = Symmetric.decipher(K_AB1, "AES", encryptedClientId);
            String decryptedPackageId = Symmetric.decipher(K_AB1, "AES", encryptedPackageId);

            int clientId = Integer.parseInt(decryptedClientId);
            System.out.println("uid: " + clientId);
            int packageId = Integer.parseInt(decryptedPackageId);
            System.out.println("paquete_id: " + packageId);

            String hmacClientIdGen = Symmetric.generateHMAC(K_AB2, decryptedClientId);
            String hmacPackageIdGen = Symmetric.generateHMAC(K_AB2, decryptedPackageId);

            String decryptedHmacClientId = Base64.getEncoder().encodeToString(hmacClientId.getBytes());
            String decryptedHmacPackageId = Base64.getEncoder().encodeToString(hmacPackageId.getBytes());

            if (decryptedHmacClientId.equals(hmacClientIdGen) && decryptedHmacPackageId.equals(hmacPackageIdGen)) {
                System.out.println("HMACs verificados");
                writer.write("OK");
            } else {
                System.out.println("HMACs no verificados");
                writer.write("ERROR");
            }

            inputLine = reader.readLine();
        }
        return inputLine;
    }
}
