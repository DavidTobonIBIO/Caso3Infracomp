package symmetric;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String[] generatePG(String openSSLPath) throws IOException, InterruptedException{
        Process process = Runtime.getRuntime().exec(openSSLPath + "\\openssl dhparam -text 1024");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();

	    while ((line = reader.readLine()) != null) {
	        output.append(line).append("\n");
	    }
	    reader.close();
	    process.waitFor();
        
        //System.out.println(output);

        String prime = "prime:\\s*([0-9a-fA-F:]+(\\s*[0-9a-fA-F:]+)*)";

        Pattern p = Pattern.compile(prime);
        Matcher matcherP = p.matcher(output);
        String pString = "";
        String gString = "";

        if (matcherP.find()){
            pString = matcherP.group(1);
            pString = pString.replaceAll("\n", "").replaceAll(" ", "");
        }else{
            System.out.println("P not found");
        }
            
        String generator = "generator:\\s*(\\d+)";
        Pattern g = Pattern.compile(generator);
        Matcher matcherG = g.matcher(output);
        
        if (matcherG.find()){
            gString = matcherG.group(1);
        }else{
            System.out.println("G not found");
        }

        String[] GP = new String[] {pString, gString};
        return GP;
    }

    public static BigInteger parser(String P){
        String[] partes = P.split(":");
        String newS = "";

        for (int i = 0; i < partes.length; i++){
            int parse = Integer.parseInt(partes[i], 16);
            String bin = Integer.toBinaryString(parse);
            newS = newS + bin;
        }
        //System.out.println(newS);
        BigInteger resp = new BigInteger(newS, 2);
        return resp;
    }

    public static BigInteger[] generateY(BigInteger P, int G){
        Random rand = new Random();
        BigInteger GB = BigInteger.valueOf(G);
        BigInteger x1 = new BigInteger(1022, rand);
        BigInteger y = GB.modPow(x1, P);
        BigInteger[] resp = new BigInteger[] {y, x1};
        return resp;

    }

    public static SecretKey loadKey(String algorithm) throws Exception {
        String keyContent = new String(Files.readAllBytes(Paths.get(KEY_FILE_PATH)));
        byte[] decodedKey = Base64.getDecoder().decode(keyContent);

        return new SecretKeySpec(decodedKey, algorithm);
    }
}
