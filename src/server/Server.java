package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.SecretKey;

import asymmetric.Asymmetric;
import symmetric.Symmetric;

public class Server {

    private static final int PORT = 3400;
    private static final int MAX_CLIENTS = 8;

    private boolean continueFlag = true;
    private int nThreads;
    private static PrivateKey privateKey;
        private SecretKey symmetricKey;
    
        public void launchWithConsole() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
            Scanner scanner = new Scanner(System.in);
            boolean inMenu = true;
    
            while (inMenu) {
                System.out.println("\nBienvenido al SERVIDOR, seleccione una de las opciones: \n1. Generar pareja de llaves \n2. Ejecutar \n0. Salir");
                int option = scanner.nextInt();
                switch (option) {
                    case 1:
                        System.out.println("Generando llaves...");
                        generateKeys();
                        
                        
                        break;
                    case 2:
                        boolean check = true;
                        while (check) {
                            System.out.println(
                                    "Ingrese el numero de delegados concurrentes (1 para proceso iterativo, 4, 8, 32):");
                            nThreads = scanner.nextInt();
                            if (nThreads > 0) {
                                check = false;
                                launch();
                            } else {
                                System.out.println("Número de delegados no permitido");
                            }
                        }
                        inMenu = false;
                        break;
                    case 0:
                        System.out.println("Saliendo...");
                        inMenu = false;
                        break;
                    default:
                        System.out.println("Opción no válida");
                        break;
                }
    
            }
            scanner.close();
    
        }
    
        public void setUp(int nDelegates) {
            this.nThreads = nDelegates;
            generateKeys();
        }
    
        private void generateKeys() {
            Symmetric.generateKeys("AES");
            Asymmetric.generateKeys("RSA");
        }
        
        public static PrivateKey getPrivateKey(){
            return privateKey;
    }

    public void launch() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        System.out.println("Inicializando Servidor...");
        try {
            privateKey = Asymmetric.loadPrivateKey("RSA");
            symmetricKey = Symmetric.loadKey("AES");
        } catch (Exception e) {
            System.out.println("Error al cargar las llaves");
            e.printStackTrace();
            System.exit(-1);
        }
        if (nThreads == 1) {
            startMonothreadServer();
        } else {
            startMultithreadServer();
        }
    }

    private void startMonothreadServer() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor Monothread esperando conexión");
            Socket socket = serverSocket.accept();
            System.out.println("Cliente conectado: " + socket.getInetAddress().getHostAddress());

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (ServerProtocol.execute(reader, writer));
                
            writer.close();
            reader.close();
            socket.close();
            serverSocket.close();

            System.out.println("Servidor finalizado");
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor");
            e.printStackTrace();
            System.exit(-1);
        }

    }

    private void startMultithreadServer() {
        ServerSocket serverSocket = null;
        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);

        try {
            serverSocket = new ServerSocket(PORT);
            int id = 1;

            while (continueFlag) {
                System.out.println("Servidor Multithread esperando conexión");
                Socket socket = serverSocket.accept();
                System.out.println("Cliente " + id + " conectado: " + socket.getInetAddress().getHostAddress());

                ServerThread serverThread = new ServerThread(socket, id);
                threadPool.execute(serverThread);
                id++;
                if (id > MAX_CLIENTS) {
                    System.out.println("Limite de clientes alcanzado. No se aceptarán más conexiones.");
                    continueFlag = false;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor");
            e.printStackTrace();
        } finally {
            if (threadPool != null && !threadPool.isShutdown()) {
                threadPool.shutdown();
            }
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Error al cerrar el servidor");
                e.printStackTrace();
            }
            System.out.println("Servidor finalizado.");
        }
    }
}
