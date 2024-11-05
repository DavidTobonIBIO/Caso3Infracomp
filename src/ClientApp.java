import client.Client;

public class ClientApp {
    public static void main(String[] args) throws Exception {
        Client client = new Client(0);
        client.start();
    }
}
