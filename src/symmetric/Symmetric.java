package symmetric;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
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

    public static String[] generatePG(String openSSLPath) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(openSSLPath + "\\openssl dhparam -text 1024");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        reader.close();
        process.waitFor();

        String prime = "prime:\\s*([0-9a-fA-F:]+(\\s*[0-9a-fA-F:]+)*)";

        Pattern p = Pattern.compile(prime);
        Matcher matcherP = p.matcher(output);
        String pString = "";
        String gString = "";

        if (matcherP.find()) {
            pString = matcherP.group(1);
            pString = pString.replaceAll("\n", "").replaceAll(" ", "");
        } else {
            System.out.println("P not found");
        }

        String generator = "generator:\\s*(\\d+)";
        Pattern g = Pattern.compile(generator);
        Matcher matcherG = g.matcher(output);

        if (matcherG.find()) {
            gString = matcherG.group(1);
        } else {
            System.out.println("G not found");
        }

        String[] GP = new String[] { pString, gString };
        return GP;
    }

    public static BigInteger parser(String P) {
        String[] partes = P.split(":");
        String newS = "";

        for (int i = 0; i < partes.length; i++) {
            int parse = Integer.parseInt(partes[i], 16);
            String bin = Integer.toBinaryString(parse);
            newS = newS + bin;
        }
        BigInteger resp = new BigInteger(newS, 2);
        return resp;
    }

    public static BigInteger[] generateY(BigInteger P, int G) {
        Random rand = new Random();
        BigInteger GB = BigInteger.valueOf(G);
        BigInteger x1 = new BigInteger(1022, rand);
        BigInteger y = GB.modPow(x1, P);
        BigInteger[] resp = new BigInteger[] { y, x1 };
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
                System.arraycopy(combined, 0, ivBytes, 0, ivBytes.length);
                IvParameterSpec iv = new IvParameterSpec(ivBytes);
    
                // Decifrar el mensaje que son los bytes restantes
                byte[] encryptedBytes = new byte[combined.length - ivBytes.length];
                System.arraycopy(combined, ivBytes.length, encryptedBytes, 0, encryptedBytes.length);
    
                cipher.init(Cipher.DECRYPT_MODE, key, iv);
    
                byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
                return Base64.getEncoder().encodeToString(decryptedBytes);
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
            byte[] hmacBytes = mac.doFinal(clientId.getBytes());
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            System.out.println("Error al generar HMAC");
            e.printStackTrace();
            return null;
        }
    }
}
