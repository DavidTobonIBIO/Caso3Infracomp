package symmetric;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Symmetric {

    private static final String KEY_FILE_PATH = "keys/symmetric.key";
    private final static String PADDING = "AES/CBC/PKCS5Padding";

    public static void generateKeys(String algorithm) {
        System.out.println("Generando llave simétrica...");
        try {
            KeyGenerator generator = KeyGenerator.getInstance(algorithm);
            if (algorithm.equals("AES")) {
                generator.init(256);
            }
            SecretKey key = generator.generateKey();
            writeKeyToFile(key);
        } catch (Exception e) {
            System.out.println("Error al generar llave simétrica");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void writeKeyToFile(SecretKey key) {
        System.out.println("Escribiendo llave en archivo");
        try {
            FileOutputStream fos = new FileOutputStream(KEY_FILE_PATH);
            String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
            fos.write(encodedKey.getBytes());
            System.out.println("Llave escrita en el archivo " + KEY_FILE_PATH);
            fos.close();
        } catch (IOException e) {
            System.out.println("Error al escribir llave en archivo");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static DHParameterSpec generatePG() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();
        DHParameterSpec dhParams = publicKey.getParams();
        return dhParams;
    }

    public static BigInteger[] generateY(BigInteger P, int G) {
        BigInteger GB = BigInteger.valueOf(G);
        BigInteger x1 = new BigInteger(1022, new SecureRandom());
        BigInteger y = GB.modPow(x1, P);
        return new BigInteger[] { y, x1 };
    }

    public static BigInteger parser(String P) {
        String newS = P.replace(":", "");

        BigInteger resp = new BigInteger(newS, 16); // Specify base 16 for hexadecimal
        return resp;
    }

    public static SecretKey loadKey(String algorithm) throws Exception {
        String keyContent = new String(Files.readAllBytes(Paths.get(KEY_FILE_PATH)));
        byte[] decodedKey = Base64.getDecoder().decode(keyContent);

        return new SecretKeySpec(decodedKey, algorithm);
    }

    public static String cipher(SecretKey key, String algorithm, String msg) {
        try {
            if (algorithm.equals("AES")) {
                Cipher cipher = Cipher.getInstance(PADDING);
                byte[] ivBytes = new byte[16];
                SecureRandom secureRandom = new SecureRandom();
                secureRandom.nextBytes(ivBytes);
                IvParameterSpec iv = new IvParameterSpec(ivBytes);
                cipher.init(Cipher.ENCRYPT_MODE, key, iv);

                byte[] encryptedBytes = cipher.doFinal(msg.getBytes());
                byte[] combined = new byte[ivBytes.length + encryptedBytes.length];
                // Concatenar iv y mensaje cifrado
                for (int i = 0; i < ivBytes.length; i++) {
                    combined[i] = ivBytes[i];
                }
                for (int i = 0; i < encryptedBytes.length; i++) {
                    combined[i + ivBytes.length] = encryptedBytes[i];
                }

                System.out.println("Mensaje cifrado: " + Base64.getEncoder().encodeToString(combined));

                return Base64.getEncoder().encodeToString(combined);
            }
        } catch (Exception e) {
            System.out.println("Error al cifrar mensaje");
            e.printStackTrace();
        }
        return null;
    }

    public static String decipher(SecretKey key, String algorithm, String msg) {
        try {
            if (algorithm.equals("AES")) {
                Cipher cipher = Cipher.getInstance(PADDING);

                byte[] combined = Base64.getDecoder().decode(msg);

                // Extraer iv que son los primeros 16 bytes
                byte[] ivBytes = new byte[16];
                for (int i = 0; i < ivBytes.length; i++) {
                    ivBytes[i] = combined[i];
                }
                IvParameterSpec iv = new IvParameterSpec(ivBytes);

                // Decifrar el mensaje que son los bytes restantes
                byte[] encryptedBytes = new byte[combined.length - ivBytes.length];
                for (int i = 0; i < encryptedBytes.length; i++) {
                    encryptedBytes[i] = combined[i + ivBytes.length];
                }

                cipher.init(Cipher.DECRYPT_MODE, key, iv);

                byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
                String decryptedString = new String(decryptedBytes, "UTF-8");
                return decryptedString;
            }
        } catch (Exception e) {
            System.out.println("Error al descifrar mensaje");
            e.printStackTrace();
        }
        return null;
    }

    public static String generateHMAC(SecretKey key, String clientId) {
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA384");
            mac.init(key);
            byte[] hmacBytes = mac.doFinal(Base64.getEncoder().encode(clientId.getBytes()));
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            System.out.println("Error al generar HMAC");
            e.printStackTrace();
            return null;
        }
    }
}
