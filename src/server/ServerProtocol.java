package server;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;

import SHA.SHA1RSA;
import SHA.SHA512;
import asymmetric.Asymmetric;
import pkg.PackageState;
import pkg.PackageTable;
import symmetric.Symmetric;

public class ServerProtocol {

    private static PackageTable packageTable = new PackageTable();

    private BigInteger Y;
    private BigInteger P;
    private int G;
    private BigInteger x;

    private SecretKey K_AB1;
    private SecretKey K_AB2;
    private int numConcurrentClients = 0;

    public ServerProtocol(int numConcurrentClients) {
        this.numConcurrentClients = numConcurrentClients;
    }


    public boolean execute(BufferedReader reader, PrintWriter writer) throws IOException, InvalidKeyException,
            NoSuchAlgorithmException, SignatureException, InvalidKeySpecException {
        String inputLine = reader.readLine();
        if (inputLine.equals("SECINIT")) {
            System.out.println("Cliente ha iniciado comunicacion segura");
            getReto(reader, writer);
            inputLine = reader.readLine();
            if (inputLine.equals("OK")) {
                System.out.println("Cliente ha respondido correctamente al reto");
                diffieHellman(writer);
                inputLine = reader.readLine();
                if (inputLine.equals("OK")) {
                    System.out.println("Cliente ha verificado la firma");
                    getMasterKey(writer, reader);
                    inputLine = getPackageQuery(reader, writer);
                    if (inputLine.equals("TERMINAR")) {
                        System.out.println("Cliente ha solicitado desconexion.");
                        writer.println("Desconexion exitosa");
                    }
                } else {
                    System.out.println("Falla en la verificacion de la firma");
                }
            } else {
                System.out.println("Cliente no ha respondido correctamente al reto");
            }
        }
        return false;
    }

    private void diffieHellman(PrintWriter writer)
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        try {
            if (numConcurrentClients == 1) {
                // repetir para obtener replicas de tiempo y hacer promedio si se corre con
                // cliente iterativo
                for (int i = 0; i < 32; i++) {
                    long startTimeNs = System.nanoTime();
                    DHParameterSpec dhParams = Symmetric.generatePG();
                    P = dhParams.getP();
                    G = dhParams.getG().intValue();
                    System.out.println("Llaves simetricas generadas");
                    BigInteger[] YX = Symmetric.generateY(P, G);
                    Y = YX[0];
                    x = YX[1];
                    writeDiffieHellmanGenerationTime(System.nanoTime() - startTimeNs);
                }
            } else {
                long startTimeNs = System.nanoTime();
                DHParameterSpec dhParams = Symmetric.generatePG();
                P = dhParams.getP();
                G = dhParams.getG().intValue();
                System.out.println("Llaves simetricas generadas");
                BigInteger[] YX = Symmetric.generateY(P, G);
                Y = YX[0];
                x = YX[1];
                writeDiffieHellmanGenerationTime(System.nanoTime() - startTimeNs);
            }
            writer.println(String.valueOf(G));
            writer.println(String.valueOf(P));
            writer.println(String.valueOf(Y));
            sign(P, Y, G, writer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PrivateKey getPrivateKey() {
        PrivateKey privateKey = Server.getPrivateKey();
        return privateKey;
    }

    private void sign(BigInteger P, BigInteger Y, int G, PrintWriter writer)
            throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        String pString = String.valueOf(P);
        String gString = String.valueOf(G);
        String yString = String.valueOf(Y);

        String message = pString + gString + yString;
        byte[] firma = SHA1RSA.sign(getPrivateKey(), message);

        String firmaString = Base64.getEncoder().encodeToString(firma);
        writer.println(firmaString);
    }

    public synchronized void getMasterKey(PrintWriter writer, BufferedReader reader)
        throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String YClient = reader.readLine();
        BigInteger YClient_int = new BigInteger(YClient);
        BigInteger master = YClient_int.modPow(x, P);
        SecretKey[] masterKeys = SHA512.encrypt(String.valueOf(master));
        K_AB1 = masterKeys[0];
        K_AB2 = masterKeys[1];
        String encodedK_AB1 = Base64.getEncoder().encodeToString(K_AB1.getEncoded());
        String encodedK_AB2 = Base64.getEncoder().encodeToString(K_AB2.getEncoded());
        System.out.println(encodedK_AB1);
        System.out.println(encodedK_AB2);
    }

    public void getReto(BufferedReader reader, PrintWriter writer) throws IOException {
        String reto = reader.readLine();
        System.out.println(reto);
        PrivateKey privateKey = getPrivateKey();
        byte[] rta = null;
        // Repetir decifrado para obtener replicas de tiempo y hacer promedio si se
        // corre con cliente iterativo
        if (numConcurrentClients == 1) {
            for (int i = 0; i < 32; i++) {
                long startTimeNs = System.nanoTime();
                rta = Asymmetric.decipher(privateKey, "RSA", reto);
                writeRetoDecryptionTime(System.nanoTime() - startTimeNs);
            }
        } else {
            long startTimeNs = System.nanoTime();
            rta = Asymmetric.decipher(privateKey, "RSA", reto);
            writeRetoDecryptionTime(System.nanoTime() - startTimeNs);
        }
        String encodedK_AB1 = Base64.getEncoder().withoutPadding().encodeToString(rta);
        ;
        System.out.println("rta: " + encodedK_AB1);
        writer.println(encodedK_AB1);
    }

    public String getPackageQuery(BufferedReader reader, PrintWriter writer) throws IOException {
        String inputLine = reader.readLine();
        while (!inputLine.equals("TERMINAR")) {

            verifyQuery(inputLine, reader, writer);

            inputLine = reader.readLine();
        }
        return inputLine;
    }

    private void verifyQuery(String inputLine, BufferedReader reader, PrintWriter writer) throws IOException {
        long startTimeNs = System.nanoTime();

        String encryptedClientId = inputLine;
        String hmacClientId = reader.readLine();
        String encryptedPackageId = reader.readLine();
        String hmacPackageId = reader.readLine();

        System.out.println("C(K_AB1, uid): " + encryptedClientId);
        System.out.println("HMAC(K_AB2, uid): " + hmacClientId);
        System.out.println("C(K_AB1, paquete_id): " + encryptedPackageId);
        System.out.println("HMAC(K_AB2, paquete_id): " + hmacPackageId);

        String decryptedClientId = Symmetric.decipher(K_AB1, "AES", encryptedClientId);
        String decryptedPackageId = Symmetric.decipher(K_AB1, "AES", encryptedPackageId);

        System.out.println("uid: " + decryptedClientId);
        System.out.println("paquete_id: " + decryptedPackageId);

        String hmacClientIdGen = Symmetric.generateHMAC(K_AB2, decryptedClientId);
        String hmacPackageIdGen = Symmetric.generateHMAC(K_AB2, decryptedPackageId);
        boolean verification = hmacClientId.equals(hmacClientIdGen) && hmacPackageId.equals(hmacPackageIdGen);
        writeQueryVerificationTime(System.nanoTime() - startTimeNs);
        if (verification) {
            System.out.println("HMACs verificados");
            writer.println("OK");
            sendPackageState(decryptedClientId, decryptedPackageId, writer);
        } else {
            System.out.println("HMACs no verificados");
            writer.println("ERROR");
        }
    }

    private void sendPackageState(String clientId, String packageId, PrintWriter writer) {
        PackageState pkg = packageTable.getPackageState(Integer.parseInt(clientId), Integer.parseInt(packageId));
        String pkgState = String.valueOf(pkg.getCode());
        System.out.println("Estado del paquete: " + pkgState);

        long startTimeNs = System.nanoTime();
        String encryptedState = Symmetric.cipher(K_AB1, "AES", pkgState);
        writePackageStateCipherTime(System.nanoTime() - startTimeNs, true);

        // tomar tiempo solo para comparar, no es parte del protocolo
        startTimeNs = System.nanoTime();
        Asymmetric.cipher(getPrivateKey(), "RSA", Base64.getEncoder().encodeToString(pkgState.getBytes()));
        writePackageStateCipherTime(System.nanoTime() - startTimeNs, false);

        String hmacState = Symmetric.generateHMAC(K_AB2, pkgState);
        System.out.println("C(K_AB1, " + pkgState + "): " + encryptedState);
        System.out.println("HMAC(K_AB2, " + pkgState + "): " + hmacState);
        writer.println(encryptedState);
        writer.println(hmacState);
    }

    private void writeRetoDecryptionTime(long timeNs) {
        try {
            FileWriter writer = new FileWriter("reto.csv", true);
            writer.append(String.valueOf(numConcurrentClients));
            writer.append(";");
            writer.append(String.valueOf(timeNs));
            writer.append("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDiffieHellmanGenerationTime(long timeNs) {
        try {
            FileWriter writer = new FileWriter("diffie_hellman.csv", true);
            writer.append(String.valueOf(numConcurrentClients));
            writer.append(";");
            writer.append(String.valueOf(timeNs));
            writer.append("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeQueryVerificationTime(long tomeNs) {
        try {
            FileWriter writer = new FileWriter("query_verification.csv", true);
            writer.append(String.valueOf(numConcurrentClients));
            writer.append(";");
            writer.append(String.valueOf(tomeNs));
            writer.append("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writePackageStateCipherTime(long timeNs, boolean isSymmetric) {
        try {
            FileWriter writer = new FileWriter("pkg_state_cipher.csv", true);
            writer.append(String.valueOf(numConcurrentClients));
            writer.append(";");
            int isSymmetricInt = isSymmetric ? 1 : 0;
            writer.append(String.valueOf(isSymmetricInt));
            writer.append(";");
            writer.append(String.valueOf(timeNs));
            writer.append("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
