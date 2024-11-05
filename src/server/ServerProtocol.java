package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;

import SHA1RSA.SHA1RSA;
import symmetric.Symmetric;

public class ServerProtocol {

    public static boolean execute(BufferedReader reader, PrintWriter writer) throws IOException {
        String inputLine;
        String outputLine;

        inputLine = reader.readLine();
        System.out.println("Entrada: " + inputLine);
        
        if (inputLine.equals("publickey")) {
            sendPublicKey(writer);
            return true;
        } else if (inputLine.equals("symmetrickey")) {
            sendSymmetricKey(writer);
            return true;
        } else if (inputLine.equals("exit")) {
            outputLine = "exit";
            writer.println(outputLine);
            return false;
        } else if(inputLine.equals("diffie")) {
           diffieHellman(writer);
           return false;
        }
        return true;
    }

    private static void sendPublicKey(PrintWriter writer) {
        System.out.println("Enviando llave publica");
        writer.println("publickey");
    }

    private static void sendSymmetricKey(PrintWriter writer) {
        System.out.println("Enviando llave simetrica");
        writer.println("symmetrickey");
    }

    private static void diffieHellman(PrintWriter writer){
        try {
            String[] GP = Symmetric.generatePG("C:\\Users\\laura\\Downloads\\OpenSSL-1.1.1h_win32\\OpenSSL-1.1.1h_win32");
            System.out.println("Llaves generadas");
            int G = Integer.parseInt(GP[1]);
            BigInteger P = Symmetric.parser(GP[0]);
            writer.println("G:" + String.valueOf(G));
            writer.println("P:" + String.valueOf(P));
            BigInteger Y = Symmetric.generateY(P, G);
            System.out.println("Y:" + String.valueOf(Y));
            writer.println("Y:" + String.valueOf(Y));
            sign(P, Y, G);

        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void getPrivateKey(){

    }

    private static void sign(BigInteger P, BigInteger Y, int G){
        String pString = String.valueOf(P);
        String gString = String.valueOf(G);
        String yString = String.valueOf(Y);

        String message = pString + gString + yString;
        //SHA1RSA.sign(null, message);

    }
    
}
