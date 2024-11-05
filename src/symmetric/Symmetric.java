package symmetric;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Symmetric {

    private static final String KEY_FILE_PATH = "keys/symmetric.key";

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

    public static SecretKey loadKey(String algorithm) throws Exception {
        String keyContent = new String(Files.readAllBytes(Paths.get(KEY_FILE_PATH)));
        byte[] decodedKey = Base64.getDecoder().decode(keyContent);

        return new SecretKeySpec(decodedKey, algorithm);
    }
}
