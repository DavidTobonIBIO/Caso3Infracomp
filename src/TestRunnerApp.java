import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import client.Client;
import server.Server;

public class TestRunnerApp {

    private static final int NUM_TESTS = 5;
    private static final int[] CONCURRENT_CLIENTS = { 1, 4, 8, 32 };
    private static final int TOTAL_CLIENTS = 32;
    private static final String RESULTS_FILE = "results.csv";

    public static void main(String[] args) {
        createOutputFile();
    }

    public static void createOutputFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RESULTS_FILE))) {
            writer.println("encryption_algorithm,num_clients,avg_time_s,avg_time_ms,avg_time_ns");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeOutput(TestInfo testInfo) {
        try (FileWriter writer = new FileWriter(RESULTS_FILE, true)) {
            writer.append(testInfo.getEncryptionAlgorithm());
            writer.append(';');
            writer.append(String.valueOf(testInfo.getNumClients()));
            writer.append(';');
            writer.append(String.valueOf(testInfo.getAvgTimeS()));
            writer.append(';');
            writer.append(String.valueOf(testInfo.getAvgTimeMs()));
            writer.append(';');
            writer.append(String.valueOf(testInfo.getAvgTimeNs()));
            writer.append('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
