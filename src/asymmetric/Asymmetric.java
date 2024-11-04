package asymmetric;

import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class Asymmetric {

    private static final String PUBLIC_KEY_FILE_PATH = "keys/public.key";
    private static final String PRIVATE_KEY_FILE_PATH = "keys/private.key";

    public static void generateKeys(String algorithm) {
        System.out.println("Generando llaves asimétricas con algoritmo " + algorithm);
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm);
            int keySize = 0;
            if (algorithm.equals("RSA")) {
                keySize = 1024;
            }
            generator.initialize(keySize);
            KeyPair keyPair = generator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            writeKeysToFile(publicKey, privateKey);

        } catch (Exception e) {
            System.out.println("Error al generar llaves asimétricas");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void writeKeysToFile(PublicKey publicKey, PrivateKey privateKey) {
        System.out.println("Escribiendo llaves en archivos");
        try {
            FileOutputStream publicKeyFos = new FileOutputStream(PUBLIC_KEY_FILE_PATH);
            String encodedKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            publicKeyFos.write(encodedKey.getBytes());
            publicKeyFos.close();
            System.out.println("Llave publica escrita en el archivo " + PUBLIC_KEY_FILE_PATH);

            FileOutputStream privateKeyFos = new FileOutputStream(PRIVATE_KEY_FILE_PATH);
            encodedKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            privateKeyFos.write(encodedKey.getBytes());
            privateKeyFos.close();
            System.out.println("Llave privada escrita en el archivo " + PRIVATE_KEY_FILE_PATH);
        } catch (Exception e) {
            System.out.println("Error al escribir llaves en archivos");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
