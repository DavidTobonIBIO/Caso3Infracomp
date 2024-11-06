package asymmetric;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

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

    public static PublicKey loadPublicKey(String algorithm) throws Exception {
        String publicKeyContent = new String(Files.readAllBytes(Paths.get(PUBLIC_KEY_FILE_PATH)));
        byte[] decodedKey = Base64.getDecoder().decode(publicKeyContent);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey loadPrivateKey(String algorithm) throws Exception {
        String privateKeyContent = new String(Files.readAllBytes(Paths.get(PRIVATE_KEY_FILE_PATH)));
        byte[] decodedKey = Base64.getDecoder().decode(privateKeyContent);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePrivate(keySpec);
    }

    public static byte[] cipher(Key key, String algorithm, String msg) {
        byte[] encryptedMsg = null;
        byte[] mensajeBytes = Base64.getDecoder().decode(msg);
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedMsg = cipher.doFinal(mensajeBytes);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return encryptedMsg;
    }

    public static byte[] decipher(Key key, String algorithm, String msg) {
        byte[] decryptedMsg = null;
        byte[] msgBytes = Base64.getDecoder().decode(msg);

        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedMsg = cipher.doFinal(msgBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedMsg;
    }
}
