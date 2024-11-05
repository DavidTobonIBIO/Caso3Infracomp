import java.util.Scanner;

import client.Client;

public class ClientApp {

    private static final int NUM_ITERATIONS = 32;

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        boolean inMenu = true;

        while (inMenu) {
            System.out.println(
                    "Bienvenido al CLIENTE, seleccione una de las opciones: \n1. Ejecutar cliente iterativo\n2. Ejecutar cliente concurrente\n0. Salir");
            int option = scanner.nextInt();
            if (option == 1) {
                System.out.println("Inicializando Cliente Iterativo...");
                Client client = new Client(0, 0);
                client.startIterativeClient();
                inMenu = false;
            } else if (option == 2) {
                System.out.println("Inicializando Clientes Concurrentes...");
                Thread[] clients = new Thread[NUM_ITERATIONS];
                for (int i = 0; i < clients.length; i++) {
                    Client client = new Client(i, i);
                    clients[i] = client;
                    client.start();
                }
                for (int i = 0; i < clients.length; i++) {
                    clients[i].join();
                }
                inMenu = false;
            } else if (option == 0) {
                System.out.println("Gracias por usar el sistema");
                inMenu = false;
            } else {
                System.out.println("Opción no válida");
            }
        }
        scanner.close();
    }
}
