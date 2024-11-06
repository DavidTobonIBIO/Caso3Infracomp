import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import client.Client;
import server.Server;

public class TestRunnerApp {

    public static void createOutputFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("xd.csv"))) {
            writer.println("encryption_algorithm,num_clients,avg_time_s,avg_time_ms,avg_time_ns");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeOutput() {
        try (FileWriter writer = new FileWriter("xd.csv", true)) {
            writer.append("ssss");
            writer.append(';');
            writer.append("ssss");
            writer.append(';');
            writer.append("ssss");
            writer.append('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
