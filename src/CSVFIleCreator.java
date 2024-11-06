import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CSVFIleCreator {
    
    public static void main(String[] args) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("reto.csv"))) {
            writer.println("n_concurrent;time_ns");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter("diffie_hellman.csv"))) {
            writer.println("n_concurrent;time_ns");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter("query.csv"))) {
            writer.println("n_concurrent;time_ns");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter("cipher_pkg_state.csv"))) {
            writer.println("n_concurrent;is_symmetric;time_ns");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
