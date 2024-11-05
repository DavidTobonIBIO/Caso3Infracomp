package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import SHA.SHA1RSA;
import SHA.SHA512;
import symmetric.Symmetric;

public class ServerProtocol {
    private static BigInteger Y;
    private static BigInteger P;
    private static int G;
    private static BigInteger x;

    private static final String OPEN_SSL_PATH = "OpenSSL-1.1.1h_win32";
    private static PublicKey K_AB1;
    private static PublicKey K_AB2;

    public static boolean execute(BufferedReader reader, PrintWriter writer) throws IOException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException {
        String inputLine = reader.readLine();
    
          
        System.out.println("Entrada: " + inputLine);
    
        switch (inputLine) {
            case "SECINIT":
                System.out.println("Cliente ha iniciado comunicación segura");
                return true;

            case "OK Reto":
                diffieHellman(writer);
                return true;
            
            case "OK Diffie-Hellman":
                getMasterKey(writer, reader);
                return true;

            case "TERMINAR":
                writer.println("Desconexión exitosa");
                System.out.println("Cliente ha solicitado desconexión.");
                return false;
    
            default:
                writer.println("Comando no reconocido");
                System.out.println("Comando no reconocido: " + inputLine);
                return true;
        }
    }

    private static void diffieHellman(PrintWriter writer) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException{
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
            //System.out.println(String.valueOf(Y));
            writer.println(String.valueOf(Y));
            sign(P, Y, G, writer);

        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static PrivateKey getPrivateKey(){
        PrivateKey privateKey = Server.getPrivateKey();
        return privateKey;
    }

    private static void sign(BigInteger P, BigInteger Y, int G, PrintWriter writer) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException{
        String pString = String.valueOf(P);
        String gString = String.valueOf(G);
        String yString = String.valueOf(Y);

        String message = pString + gString + yString;
        byte[] firma = SHA1RSA.sign(getPrivateKey(), message);
        
        String firmaString = Base64.getEncoder().encodeToString(firma);
        //System.out.println(firmaString);
        writer.println(firmaString);
    }
    
    public static void getMasterKey(PrintWriter writer, BufferedReader reader) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
        String YClient = reader.readLine();
        BigInteger YClient_int = new BigInteger(YClient);
        BigInteger master = YClient_int.modPow(x, P);
        PublicKey[] masterKeys = SHA512.encrypt(String.valueOf(master));
        K_AB1 = masterKeys[0];
        K_AB2 = masterKeys[1];
        //System.out.println(String.valueOf(master));  
    }
}
