package SHA;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA512 {
    private static String ALGORITHM = "SHA-512";
    public static String encrypt(String message) throws NoSuchAlgorithmException{
        MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
        byte[] messageByte = digest.digest(message.getBytes());
        int size = messageByte.length/2;
        byte[] K_AB1 = new byte[size];
        byte[] K_AB2 = new byte[messageByte.length - size];

        System.arraycopy(messageByte, 0, K_AB1, 0, size);
        System.arraycopy(messageByte, size, K_AB2, 0, K_AB2.length);

        
        return "";
    }
}
