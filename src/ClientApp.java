import client.Client;
import client.ClientProtocol;

public class ClientApp {
    public static void main(String[] args) throws Exception {
        ClientProtocol.loadKeys();
        Client client = new Client(0);
        client.startIterativeClient();
    }
}
