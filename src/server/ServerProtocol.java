package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

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

}
