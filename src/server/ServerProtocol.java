package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ServerProtocol {

    public static boolean execute(BufferedReader reader, PrintWriter writer) throws IOException {
        String inputLine = reader.readLine();
    
          
        System.out.println("Entrada: " + inputLine);
    
        switch (inputLine) {
            case "SECINIT":
                System.out.println("Cliente ha iniciado comunicación segura");
                return true;
                
            case "publickey":
                sendPublicKey(writer);
                return true;
    
            case "symmetrickey":
                sendSymmetricKey(writer);
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
    

    private static void sendPublicKey(PrintWriter writer) {
        System.out.println("Enviando llave publica");
        writer.println("publickey");
    }

    private static void sendSymmetricKey(PrintWriter writer) {
        System.out.println("Enviando llave simetrica");
        writer.println("symmetrickey");
    }

}
