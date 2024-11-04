package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ClientProtocol {

    public static void execute(BufferedReader reader, PrintWriter writer) throws IOException {
        requestKeys(reader, writer);
        disconnect(writer);
    }

    private static void requestKeys(BufferedReader reader, PrintWriter writer) throws IOException {
        System.out.println("Solicitando llaves al servidor");
        publicKeyRequest(writer, reader);
        symmetricKeyRequest(writer, reader);
    }

    private static void publicKeyRequest(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("publickey");
        String response = reader.readLine();
        System.out.println("Llave publica: " + response);
    }

    private static void symmetricKeyRequest(PrintWriter writer, BufferedReader reader) throws IOException {
        writer.println("symmetrickey");
        String response = reader.readLine();
        System.out.println("Llave simetrica: " + response);
    }

    private static void disconnect(PrintWriter writer) {
        writer.println("exit");
    }

}
