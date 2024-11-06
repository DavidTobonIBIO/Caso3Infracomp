import java.io.IOException;
import java.io.PrintWriter;

import server.Server;

public class ServerApp {
    public static void main(String[] args) throws Exception {
        createCSVFiles();
        Server server = new Server();
        server.launchWithConsole();
    }

    private static void createCSVFiles() {
        try {
            PrintWriter writer = new PrintWriter("reto.csv");
            writer.println("n_concurrent;time_ns");
            writer.close();

            writer = new PrintWriter("diffie_hellman.csv");
            writer.println("n_concurrent;total_time_ns;G_time_ns;P_time_ns;Gx_time_ns");
            writer.close();

            writer = new PrintWriter("query.csv");
            writer.println("n_concurrent;time_ns");
            writer.close();

            writer = new PrintWriter("cipher_pkg_state.csv");
            writer.println("n_concurrent;is_symmetric;time_ns");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
